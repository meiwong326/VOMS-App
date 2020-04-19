package com.example.voms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceView;
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
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Convergence extends AppCompatActivity /*implements CameraBridgeViewBase.CvCameraViewListener2*/ {

    private static final String TAG = "OpenCVConfiguration";

    private static final int JAVA_DETECTOR = 0;
    private static final int TM_SQDIFF = 0;
    private static final int TM_SQDIFF_NORMED = 1;
    private static final int TM_CCOEFF = 2;
    private static final int TM_CCOEFF_NORMED = 3;
    private static final int TM_CCORR = 4;
    private static final int TM_CCORR_NORMED = 5;

    private Button button;
    TextView  distanceText;
    Context context;

    float F = 1f;           //focal length
    float sensorX, sensorY; //camera sensor dimensions
    float angleX, angleY;

    JavaCameraView javaCameraView;
    File cascadeFile;
    File cascadeFileEye;
    CascadeClassifier faceDetector;
    CascadeClassifier eyeDetector;

    int learn_frames = 0;
    int method = 0;
    int detectorType = JAVA_DETECTOR;
    int absoluteFaceSize = 0;
    float relativeFaceSize = 0.2f;
    String[] detectorName;
    Mat mRgba, mGray;
    Mat teplateR, teplateL;

   /* BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) throws IOException {
            switch (status) {
                case BaseLoaderCallback.SUCCESS: {
                    javaCameraView.enableView();

                    try {
                        InputStream inputStream = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        cascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream fileOutputStream = new FileOutputStream(cascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead);
                        }
                        inputStream.close();
                        fileOutputStream.close();

                        *//*InputStream inputStreamEye = getResources().openRawResource(R.raw.haarcascade_lefteye_2splits);
                        File cascadeDirEye = getDir("cascadeEye", Context.MODE_PRIVATE);
                        cascadeFileEye = new File(cascadeDirEye, "haarcascade_lefteye_2splits.xml");
                        FileOutputStream fileOutputStreamEye = new FileOutputStream(cascadeDirEye);

                        byte[] bufferEye = new byte[4096];
                        int bytesReadEye;
                        while ((bytesReadEye = inputStreamEye.read(bufferEye)) != -1) {
                            fileOutputStreamEye.write(bufferEye, 0, bytesReadEye);
                        }
                        inputStreamEye.close();
                        fileOutputStreamEye.close();
                         *//*

                        faceDetector = new CascadeClassifier(
                                cascadeFile.getAbsolutePath());
                        if (faceDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            faceDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from "
                                    + cascadeFile.getAbsolutePath());

                        *//*eyeDetector = new CascadeClassifier(
                                cascadeFileEye.getAbsolutePath());
                        if (eyeDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            eyeDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from "
                                    + cascadeFile.getAbsolutePath());
                         *//*

                        cascadeDir.delete();
                        //cascadeFileEye.delete();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    javaCameraView.enableFpsMeter();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };*/


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

/*        javaCameraView = findViewById(R.id.camera_view);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);*/
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

    /*@Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        MatOfRect faceDetections = new MatOfRect();
        if (faceDetector != null)
            faceDetector.detectMultiScale(mRgba, faceDetections);

        for (Rect r : faceDetections.toArray()) {
            Imgproc.rectangle(mRgba, new Point(r.x, r.y),
                    new Point(r.x + r.width, r.y + r.height),
                    new Scalar(255, 0, 0));

            Rect eyearea = new Rect(r.x + r.width / 8,
                    (int) (r.y + (r.height / 4.5)), r.width - 2 * r.width / 8,
                    (int) (r.height / 3.0));

            Rect eyearea_right = new Rect(r.x + r.width / 16,
                    (int) (r.y + (r.height / 4.5)),
                    (r.width - 2 * r.width / 16) / 2, (int) (r.height / 3.0));
            Rect eyearea_left = new Rect(r.x + r.width / 16
                    + (r.width - 2 * r.width / 16) / 2,
                    (int) (r.y + (r.height / 4.5)),
                    (r.width - 2 * r.width / 16) / 2, (int) (r.height / 3.0));

            Imgproc.rectangle(mRgba, eyearea_left.tl(), eyearea_left.br(),
                    new Scalar(255, 0, 0), 2);
            Imgproc.rectangle(mRgba, eyearea_right.tl(), eyearea_right.br(),
                    new Scalar(255, 0, 0), 2);

            *//* if (learn_frames < 5) {
                teplateR = get_template(eyeDetector, eyearea_right, 24);
                teplateL = get_template(eyeDetector, eyearea_left, 24);
                learn_frames++;
            } else {
                // Learning finished, use the new templates for template
                // matching
                match_eye(eyearea_right, teplateR, method);
                match_eye(eyearea_left, teplateL, method);
            }
             *//*


        }

        return mRgba;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    private void match_eye(Rect area, Mat mTemplate, int type) {
        Point matchLoc;
        Mat mROI = mGray.submat(area);
        int result_cols = mROI.cols() - mTemplate.cols() + 1;
        int result_rows = mROI.rows() - mTemplate.rows() + 1;
        // Check for bad template size
        if (mTemplate.cols() == 0 || mTemplate.rows() == 0) {
            return ;
        }
        Mat mResult = new Mat(result_cols, result_rows, CvType.CV_8U);

        switch (type) {
            case TM_SQDIFF:
                Imgproc.matchTemplate(mROI, mTemplate, mResult, Imgproc.TM_SQDIFF);
                break;
            case TM_SQDIFF_NORMED:
                Imgproc.matchTemplate(mROI, mTemplate, mResult,
                        Imgproc.TM_SQDIFF_NORMED);
                break;
            case TM_CCOEFF:
                Imgproc.matchTemplate(mROI, mTemplate, mResult, Imgproc.TM_CCOEFF);
                break;
            case TM_CCOEFF_NORMED:
                Imgproc.matchTemplate(mROI, mTemplate, mResult,
                        Imgproc.TM_CCOEFF_NORMED);
                break;
            case TM_CCORR:
                Imgproc.matchTemplate(mROI, mTemplate, mResult, Imgproc.TM_CCORR);
                break;
            case TM_CCORR_NORMED:
                Imgproc.matchTemplate(mROI, mTemplate, mResult,
                        Imgproc.TM_CCORR_NORMED);
                break;
        }

        Core.MinMaxLocResult mmres = Core.minMaxLoc(mResult);
        // there is difference in matching methods - best match is max/min value
        if (type == TM_SQDIFF || type == TM_SQDIFF_NORMED) {
            matchLoc = mmres.minLoc;
        } else {
            matchLoc = mmres.maxLoc;
        }
        Point matchLoc_tx = new Point(matchLoc.x + area.x, matchLoc.y + area.y);
        Point matchLoc_ty = new Point(matchLoc.x + mTemplate.cols() + area.x,
                matchLoc.y + mTemplate.rows() + area.y);

        Imgproc.rectangle(mRgba, matchLoc_tx, matchLoc_ty, new Scalar(255, 255, 0));
        Rect rec = new Rect(matchLoc_tx,matchLoc_ty);

    }

    private Mat get_template(CascadeClassifier clasificator, Rect area, int size) {
        Mat template = new Mat();
        Mat mROI = mGray.submat(area);
        MatOfRect eyes = new MatOfRect();
        Point iris = new Point();
        Rect eye_template = new Rect();
        clasificator.detectMultiScale(mROI, eyes, 1.15, 2,
                Objdetect.CASCADE_FIND_BIGGEST_OBJECT
                        | Objdetect.CASCADE_SCALE_IMAGE, new Size(30, 30),
                new Size());

        Rect[] eyesArray = eyes.toArray();
        for (int i = 0; i < eyesArray.length;) {
            Rect e = eyesArray[i];
            e.x = area.x + e.x;
            e.y = area.y + e.y;
            Rect eye_only_rectangle = new Rect((int) e.tl().x,
                    (int) (e.tl().y + e.height * 0.4), (int) e.width,
                    (int) (e.height * 0.6));
            mROI = mGray.submat(eye_only_rectangle);
            Mat vyrez = mRgba.submat(eye_only_rectangle);


            Core.MinMaxLocResult mmG = Core.minMaxLoc(mROI);

            Imgproc.circle(vyrez, mmG.minLoc, 2, new Scalar(255, 255, 255), 2);
            iris.x = mmG.minLoc.x + eye_only_rectangle.x;
            iris.y = mmG.minLoc.y + eye_only_rectangle.y;
            eye_template = new Rect((int) iris.x - size / 2, (int) iris.y
                    - size / 2, size, size);
            Imgproc.rectangle(mRgba, eye_template.tl(), eye_template.br(),
                    new Scalar(255, 0, 0), 2);
            template = (mGray.submat(eye_template)).clone();
            return template;
        }
        return template;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV configuration was a success");

            try {
                baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else{
            Log.d(TAG, "OpenCV configuration was NOT a success");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, baseLoaderCallback);
        }
    }*/

}
