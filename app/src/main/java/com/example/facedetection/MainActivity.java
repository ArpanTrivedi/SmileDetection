package com.example.facedetection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE=123;
    private FirebaseVisionImage img;
    private FirebaseVisionFaceDetector dectector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        Button camera=findViewById(R.id.button_camera);


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pic=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(pic.resolveActivity(getPackageManager())!=null){
                    startActivityForResult(pic,REQUEST_CODE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            datectFace(bitmap);
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void datectFace(Bitmap bitmap) {
        FirebaseVisionFaceDetectorOptions highAccuracyOpts =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setModeType(FirebaseVisionFaceDetectorOptions.ACCURATE_MODE)
                        .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .setMinFaceSize(0.15f)
                        .setTrackingEnabled(true)
                        .build();

        try {
            img=FirebaseVisionImage.fromBitmap(bitmap);
            dectector= FirebaseVision.getInstance().getVisionFaceDetector(highAccuracyOpts);
        } catch (Exception e) {
            e.printStackTrace();
        }

        dectector.detectInImage(img).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                String resultText="";
                int i=1;
                for(FirebaseVisionFace face:firebaseVisionFaces){
                    resultText=resultText.concat("\n"+i+" perons details.")
                            .concat("\nSmile: "+face.getSmilingProbability()*100+"%")
                            .concat("\nLeftEye :"+face.getLeftEyeOpenProbability()*100+"%")
                            .concat("\nRightEye :"+face.getRightEyeOpenProbability()*100+"%")
                            .concat("\nHeadonLeftSide :"+face.getHeadEulerAngleY()*100+"%")
                            .concat("\nHeadonRightSide :"+face.getHeadEulerAngleZ()*100+"%")
                            .concat("\n Bounding Box"+face.getBoundingBox()+" ");
                    i++;
                }
                if(firebaseVisionFaces.size()==0){
                    Toast.makeText(MainActivity.this,"NO FACE DETECTED",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Bundle bundle=new Bundle();
                    bundle.putString(ArpanFaced.RESULT_TEXT,resultText);
                    DialogFragment result=new ResultD();
                    result.setArguments(bundle);
                    result.setCancelable(false);
                    result.show(getSupportFragmentManager(),ArpanFaced.RESULT_D);
                }
            }
        });

    }
}
