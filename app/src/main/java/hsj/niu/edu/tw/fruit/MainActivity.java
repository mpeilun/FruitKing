package hsj.niu.edu.tw.fruit;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hsj.niu.edu.tw.fruit.ml.Model;

public class MainActivity extends AppCompatActivity {

    String[][] str = {
            {"蘋果", "apple"},
            {"香蕉", "banana"},
            {"芭樂", "guava"},
            {"芒果", "mango"},
            {"蓮霧", "wax_apple"},
            {"水蜜桃", "peach"},
            {"奇異果", "kiwi"},
            {"木瓜", "papaya"}
    };
    int[] img = {R.drawable.apple, R.drawable.banana, R.drawable.guava, R.drawable.mango, R.drawable.wax_apple,
            R.drawable.peach, R.drawable.kiwi, R.drawable.papaya};
    HashMap<String, String> hashMap;
    List<HashMap<String, String>> list;
    Button button;
    ListView listView;
    int imageSize = 224;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        listView = findViewById(R.id.ListView);

        list = new ArrayList<>();
        int index = 0;
        for (String[] s : str) {
            hashMap = new HashMap<>();
            hashMap.put("title", s[0]);
            hashMap.put("image", Integer.toString(img[index++]));
            list.add(hashMap);
        }

        SimpleAdapter adapter = new SimpleAdapter(this,
                list,
                R.layout.fruit_list,
                new String[]{"image", "title"},
                new int[]{R.id.imageView, R.id.txtTitle}
        );
        listView.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    //Request camera permission if we don't have it.
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

//        Intent intent = new Intent();
//        intent.setClass(MainActivity.this,MainActivity3.class);
//
//        Bundle bundle = new Bundle();
//        bundle.putString("name", str[0][0]);
//        intent.putExtras(bundle);
//        startActivity(intent);
    }

    public void classifyImage(Bitmap image) {
        try {
            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"apple", "banana", "guava", "mango", "wax_apple", "other"};
//            result.setText(classes[maxPos]);

            String s = "";
            for (int i = 0; i < classes.length; i++) {
                s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100);
            }
//            confidence.setText(s);

            if(confidences[maxPos] < 0.6){
                maxPos = 5;
            }

            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MainActivity2.class);

            Bundle bundle = new Bundle();
            bundle.putString("key", classes[maxPos]);
            intent.putExtras(bundle);
            startActivity(intent);

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
//            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}