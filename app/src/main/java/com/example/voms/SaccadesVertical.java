package com.example.voms;

/* At the moment, there is no method to analyze vertical saccadic eye movement. Below, the method used
is the haar cascade, but it is primarily used in horizontal scenarios. Another issue with this method is
more accurate in its analysis using the left eye algorithm for both the left and right eyes instead of
using the left haar cascade for the left eye and the right haar cascade for the right eye. Below, the
left haar cascade is used for both eyes, which evidently may cause discrepancies in reporting. In the
horizontal saccade test, Google's Vision API was used using the Euler Y angles. Currently Mobile Vision
supports the y and z planes, but only EulerY and EulerZ angles can be reported. EulerX in this case would
be able to calculate angles in the vertical plane, but this feature is not yet supported by Google's
Vision API. It is possible to analyze recordings of vertical eye movement, but it requires the video
to be broken down into images to analyze. A reccomendation for future use and additional development
would be to purchase the software development kit (SDK) from interactive minds. Their SDK features
extentive capabilities into eye analysis including saccadic detection as well as a preexisting eye
follower. With this took, you would be able to analyze further the rotation and movement of both eyes.

 */

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.params.Face;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

import java.io.IOException;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

public class SaccadesVertical extends AppCompatActivity {

    private Button button;
    private Float F = 1f;
    private ImageView arrow_up;
    private ImageView arrow_down;
    Context context;


    CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saccades_vertical);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        button = findViewById(R.id.saccades_vertical_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaccadesVertical.this, SaccadesVerticalSymptoms.class);
                startActivity(intent);
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            Toast.makeText(this, "Grant Permission and restart app", Toast.LENGTH_SHORT).show();
        }
        else {
            Camera camera = frontCam();
            Camera.Parameters campar = camera.getParameters();
            //F = campar.getFocalLength();
            //videoView = findViewById(R.id.videoView);
            //textView = findViewById(R.id.textView);
            arrow_up = findViewById(R.id.arrow_up);
            arrow_down = findViewById(R.id.arrow_down);
            //camera.stopPreview();
            //camera.release();
            //AngleRotation = findViewById(R.id.RotationAngleY);
            createCameraSource();

        }
    }

    private Camera frontCam() { //use front camera
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
        detector.setProcessor(new LargestFaceFocusingProcessor(detector, new SaccadesVertical.FaceTracker());

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

    //Use Google vision api to detect eyes
    public class FaceTracker extends Tracker<Face> {
        private FaceTracker() {

        }

        @Override
        public void onMissing(Detector.Detections<Face> detections) {
            super.onMissing(detections);
            showStatus("Face Not Detected");
        }

        @Override
        public void onDone() {
            super.onDone();
        }
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        CascadeClassifier cascadeFaceClassifier = new CascadeClassifier(
                "/Users/victoriaschoenig/Downloads/VOMS-App-master/app/src/main/res/raw/haarcascade_lefteye_2splits.xml");
        CascadeClassifier cascadeEyeClassifier = new CascadeClassifier(
                "/Users/victoriaschoenig/Downloads/VOMS-App-master/app/src/main/res/raw/haarcascade_lefteye_2splits.xml");
        CascadeClassifier cascadeNoseClassifier = new CascadeClassifier(
                "/Users/victoriaschoenig/Downloads/VOMS-App-master/app/src/main/res/raw/haarcascade_lefteye_2splits.xml");

        VideoCapture videoDevice = new VideoCapture();
        videoDevice.open(0);
        if (videoDevice.isOpened()) {

            while (true) {
                Mat frameCapture = new Mat();
                videoDevice.read(frameCapture);


                MatOfRect faces = new MatOfRect();
                cascadeFaceClassifier.detectMultiScale(frameCapture, faces);

                for (Rect rect : faces.toArray()) {

                    Imgproc.putText(frameCapture, "Face", new Point(rect.x,rect.y-5), 1, 2, new Scalar(0,0,255));
                    Imgproc.rectangle(frameCapture, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                            new Scalar(0, 100, 0),3);
                }

                MatOfRect eyes = new MatOfRect();
                cascadeEyeClassifier.detectMultiScale(frameCapture, eyes);
                for (Rect rect : eyes.toArray()) {

                    Imgproc.putText(frameCapture, "Eye", new Point(rect.x,rect.y-5), 1, 2, new Scalar(0,0,255));

                    Imgproc.rectangle(frameCapture, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                            new Scalar(200, 200, 100),2);
                }

                MatOfRect nose = new MatOfRect();
                cascadeNoseClassifier.detectMultiScale(frameCapture, nose);
                for (Rect rect : nose.toArray()) {

                    Imgproc.putText(frameCapture, "Nose", new Point(rect.x,rect.y-5), 1, 2, new Scalar(0,0,255));

                    Imgproc.rectangle(frameCapture, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                            new Scalar(50, 255, 50),2);
                }

                PushImage(ConvertMat2Image(frameCapture));
                System.out.println(String.format("%s (FACE) %s (EYE) %s (NOSE) detected.", faces.toArray().length,eyes.toArray().length,nose.toArray().length));
            }
        } else {
            System.out.println("Connection to device failed.");
            return;
        }
    }

    //Conversion to image
    private static BufferedImage ConvertMat2Image(Mat cameraData) {

        MatOfByte byteMatData = new MatOfByte();
        Imgcodecs.imencode(".png", cameraData, byteMatData);
        // convert to array
        byte[] byteArray = byteMatData.toArray();
        BufferedImage image = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            image = ImageIO.read(in);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return image;
    }

    //Create frame
    public static void createWindow() {
        frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(700, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    //Image label
    public static void PushImage(Image img2) {
        if (frame == null)
            CreateWindow();
        if (lbl != null)
            frame.remove(lbl);
        icon = new ImageIcon(img2);
        lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.revalidate();
    }
}



   /* private class FaceTrackerFactory implements MultiProcessor.Factory<Face> {

        private FaceTrackerFactory() {

        }

        @Override
        public Tracker<Face> create(Face face) {
            return new SaccadesVertical.EyesTracker();
        }
    }

    public void createCameraSource() {
        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        detector.setProcessor(new MultiProcessor.Builder(new SaccadesVertical.FaceTrackerFactory()).build());

        cameraSource = new CameraSource.Builder(this, detector)
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

    */

