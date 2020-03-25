package com.example.voms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SizeF;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Convergence extends AppCompatActivity implements  CvCameraViewListener2{

    private static final String TAG = "Convergence";

    private Button button;
    TextView  distanceText;
    Context context;

    float F = 1f;           //focal length
    float sensorX, sensorY; //camera sensor dimensions
    float angleX, angleY;

    private CameraBridgeViewBase openCvCameraView;
    private Mat zoomWindow;
    private CascadeClassifier javaDetectorLeftEye;
    private boolean captureFrame;
    private int count = 0;

    private Mat mRgba;
    private Mat mRgbaF;
    private Mat mRgbaT;
    private Mat mGray;
    private Mat mGrayF;
    private Mat mGrayT;
    private Mat mIntermediateMat;
    private Mat hierarchy;

    private BaseLoaderCallback loaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            switch(status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // load cascade file from application resources
                    File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);

                    javaDetectorLeftEye = loadClassifier(R.raw.haarcascade_lefteye_2splits, "haarcascade_eye_left.xml",
                            cascadeDir);

                    cascadeDir.delete();
                    openCvCameraView.setCameraIndex(0);
                    openCvCameraView.enableFpsMeter();
                    openCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;

            }

        }
    };

    static {
        if (OpenCVLoader.initDebug()) {
            Log.d("Convergence", "Configuration was a success");
        }
        else{
            Log.e("Convergence", "Configuration was not a success");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convergence);

        button = findViewById(R.id.convergence_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Convergence.this, ConvergenceSymptoms.class);
                startActivity(intent);
            }
        });

        context = getApplicationContext();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            Toast.makeText(this, "Grant Permission and restart app", Toast.LENGTH_SHORT).show();
        }
        else {
            Camera camera = frontCam();
            Camera.Parameters campar = camera.getParameters();
            F = campar.getFocalLength();
            angleX = campar.getHorizontalViewAngle();
            angleY = campar.getVerticalViewAngle();
            sensorX = (float) (Math.tan(Math.toRadians(angleX/2))*2*F);
            sensorY = (float) (Math.tan(Math.toRadians(angleY/2))*2*F);
            camera.stopPreview();
            camera.release();
            distanceText = findViewById(R.id.distanceToFace);
            createCameraSource();
        }

        openCvCameraView = findViewById(R.id.activity_surface_view);
        openCvCameraView.setCvCameraViewListener(this);
    }

    private Camera frontCam() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            Log.v("CAMID", camIdx+"");
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e("FAIL", "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }
        return cam;
    }

    public void createCameraSource() {
        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        detector.setProcessor(new LargestFaceFocusingProcessor(detector, new FaceTracker()));

        CameraSource cameraSource = new CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(1024, 768)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraSource.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }


    }

    private class FaceTracker extends Tracker<Face> {

        private FaceTracker() {

        }

        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {
            // Distance between eyes
            float p =(float) Math.sqrt(
                    (Math.pow((face.getLandmarks().get(Landmark.LEFT_EYE).getPosition().x-
                            face.getLandmarks().get(Landmark.RIGHT_EYE).getPosition().x), 2)+
                            Math.pow((face.getLandmarks().get(Landmark.LEFT_EYE).getPosition().y-
                                    face.getLandmarks().get(Landmark.RIGHT_EYE).getPosition().y), 2)));

            // Average distance between eyes
            float H = 63;

            float d = F*(H/sensorX)*(768/(2*p))/10;  // Divide by 10 to convert mm to cm

            showStatus("\ndistance: "+String.format("%.0f",d)+"cm");
        }

        @Override
        public void onMissing(Detector.Detections<Face> detections) {
            super.onMissing(detections);
            showStatus("\nface not detected");
        }

        @Override
        public void onDone() {
            super.onDone();
        }
    }

    public void showStatus(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                distanceText.setText(message);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (openCvCameraView != null)
            openCvCameraView.disableView();
    }

    // onResume()

    public void onDestroy() {
        super.onDestroy();
        openCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
        hierarchy = new Mat();
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();

        mIntermediateMat.release();
        hierarchy.release();

        zoomWindow.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (zoomWindow == null)
            createAuxiliaryMats();

        Rect area = new Rect(new Point(20, 20), new Point(mGray.width() - 20, mGray.height() - 20));
        detectEye(javaDetectorLeftEye, area, 100);

        if (captureFrame) {
            saveImage();
            captureFrame = false;
        }

        return mRgba;
    }

    private void createAuxiliaryMats() {
        if (mGray.empty())
            return;

        int rows = mGray.rows();
        int cols = mGray.cols();

        if (zoomWindow == null) {
            zoomWindow = mRgba.submat(rows / 2 + rows / 10, rows, cols / 2 + cols / 10, cols);
        }

    }

    private Mat detectEye(CascadeClassifier clasificator, Rect area, int size) {
        Mat template = new Mat();
        Mat mROI = mGray.submat(area);
        MatOfRect eyes = new MatOfRect();
        Point iris = new Point();

        //isolate the eyes first
        clasificator.detectMultiScale(mROI, eyes, 1.15, 2, Objdetect.CASCADE_FIND_BIGGEST_OBJECT
                | Objdetect.CASCADE_SCALE_IMAGE, new Size(30, 30), new Size());

        Rect[] eyesArray = eyes.toArray();
        for (int i = 0; i < eyesArray.length;) {
            Rect e = eyesArray[i];
            e.x = area.x + e.x;
            e.y = area.y + e.y;
            Rect eye_only_rectangle = new Rect((int) e.tl().x, (int) (e.tl().y + e.height * 0.4), (int) e.width,
                    (int) (e.height * 0.6));

            Core.MinMaxLocResult mmG = Core.minMaxLoc(mROI);

            iris.x = mmG.minLoc.x + eye_only_rectangle.x;
            iris.y = mmG.minLoc.y + eye_only_rectangle.y;
            Imgproc.rectangle(mRgba, eye_only_rectangle.tl(), eye_only_rectangle.br(), new Scalar(255, 255, 0, 255), 2);

            //find the pupil inside the eye rect
            detectPupil(eye_only_rectangle);

            return template;
        }

        return template;
    }

    protected void detectPupil(Rect eyeRect) {
        hierarchy = new Mat();

        Mat img = mRgba.submat(eyeRect);
        Mat img_hue = new Mat();

        Mat circles = new Mat();

        // / Convert it to hue, convert to range color, and blur to remove false
        // circles
        Imgproc.cvtColor(img, img_hue, Imgproc.COLOR_RGB2HSV);// COLOR_BGR2HSV);

        Core.inRange(img_hue, new Scalar(0, 0, 0), new Scalar(255, 255, 32), img_hue);

        Imgproc.erode(img_hue, img_hue, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));

        Imgproc.dilate(img_hue, img_hue, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(6, 6)));

        Imgproc.Canny(img_hue, img_hue, 170, 220);
        Imgproc.GaussianBlur(img_hue, img_hue, new Size(9, 9), 2, 2);
        // Apply Hough Transform to find the circles
        Imgproc.HoughCircles(img_hue, circles, Imgproc.CV_HOUGH_GRADIENT, 3, img_hue.rows(), 200, 75, 10, 25);

        if (circles.cols() > 0)
            for (int x = 0; x < circles.cols(); x++) {
                double vCircle[] = circles.get(0, x);

                if (vCircle == null)
                    break;

                Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
                int radius = (int) Math.round(vCircle[2]);

                // draw the found circle
                Imgproc.circle(img, pt, radius, new Scalar(0, 255, 0), 2);
                Imgproc.circle(img, pt, 3, new Scalar(0, 0, 255), 2);
            }
    }

    private CascadeClassifier loadClassifier(int rawResId, String filename, File cascadeDir) {
        CascadeClassifier classifier = null;
        try {
            InputStream is = getResources().openRawResource(rawResId);
            File cascadeFile = new File(cascadeDir, filename);
            FileOutputStream os = new FileOutputStream(cascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            classifier = new CascadeClassifier(cascadeFile.getAbsolutePath());
            if (classifier.empty()) {
                Log.e(TAG, "Failed to load cascade classifier");
                classifier = null;
            } else
                Log.i(TAG, "Loaded cascade classifier from " + cascadeFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
        }

        return classifier;
    }

    public void onRecreateClick(View v) {
        captureFrame = true;
    }

    public void saveImage() {
        Mat mIntermediateMat = new Mat();
        Imgproc.cvtColor(mRgba, mIntermediateMat, Imgproc.COLOR_RGBA2BGR, 3);

        File path = new File(Environment.getExternalStorageDirectory() + "/OpenCV/");
        path.mkdirs();
        File file = new File(path, "image" + count + ".png");
        count++;

        String filename = file.toString();
        Boolean bool = Imgcodecs.imwrite(filename, mIntermediateMat);

        if (bool)
            Log.i(TAG, "SUCCESS writing image to external storage");
        else
            Log.i(TAG, "Fail writing image to external storage");
    }

}
