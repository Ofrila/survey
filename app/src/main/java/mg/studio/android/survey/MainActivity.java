package mg.studio.android.survey;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.os.Environment;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import  java.util.Map;
import java.io.File;
import  java.io.IOException;

import  org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONStringer;
import  org.json.JSONException;


import  java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.InputStream;
import java.io.BufferedWriter;
import java.io.IOException;




public class MainActivity extends AppCompatActivity {
    Button btn_start, btn_next1, btn_next2, btn_next3, btn_next4, btn_next5, btn_next6, btn_next7, btn_next8, btn_next9, btn_next10, btn_next11, btn_next12, btn_finish;
    CheckBox checkbox_accept;
    TextView answer1, answer2, answer3, answer4, answer5, answer6, answer7, answer8, answer9, answer10, answer11, answer12;
    RadioGroup group1, group2, group3, group7, group8, group9, group10, group11, group12;
    EditText et6;
    List<CheckBox> list_q4 = new ArrayList<CheckBox>();
    List<CheckBox> list_q5 = new ArrayList<CheckBox>();
    String str1, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12;
    JSONObject json1, json2, json3, json4, json5, json6, json7, json8, json9, json10, json11, json12;
    JSONArray ja = new JSONArray();

    private JSONArray questions;
    private JSONObject[] answers;
    //static AppCompatActivity mainActivity;
    private int quest_num = 0;
    private int qSeq = 0;

    @Override
    /*protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome);



        //welcome
        checkbox_accept = findViewById(R.id.cb_accept);
        btn_start = (Button) findViewById(R.id.btn_start);

        btn_next1 = (Button) findViewById(R.id.btn_next1);
        btn_next2 = (Button) findViewById(R.id.btn_next2);
        btn_next3 = (Button) findViewById(R.id.btn_next3);
        btn_next4 = (Button) findViewById(R.id.btn_next4);
        btn_next5 = (Button) findViewById(R.id.btn_next5);
        btn_next6 = (Button) findViewById(R.id.btn_next6);
        btn_next7 = (Button) findViewById(R.id.btn_next7);
        btn_next8 = (Button) findViewById(R.id.btn_next8);
        btn_next9 = (Button) findViewById(R.id.btn_next9);
        btn_next10 = (Button) findViewById(R.id.btn_next10);
        btn_next11 = (Button) findViewById(R.id.btn_next11);
        btn_next12 = (Button) findViewById(R.id.btn_next12);
        btn_finish = (Button) findViewById(R.id.btn_finish);




    }*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
       checkbox_accept = (CheckBox) findViewById(R.id.cb_accept);
       // mainActivity = this;
        // initial question list
        questions = GetQuestions();
        if (questions == null) {
            quest_num = 0;
            answers = null;
        } else {
            quest_num = questions.length();
            answers = new JSONObject[quest_num];
        }
        qSeq = 0;
    }

    private JSONArray GetQuestions() {
        try {
            // read sample.json
            InputStreamReader inputReader = new InputStreamReader(
                    getAssets().open("sample.json"));
            BufferedReader buffReader = new BufferedReader(inputReader);
            String line ;
            StringBuilder text = new StringBuilder();
            while ((line = buffReader.readLine()) != null) {
                text.append(line.trim());
            }
            inputReader.close();
            // analyse text
            JSONObject json = new JSONObject(text.toString());
            JSONObject survey = json.getJSONObject("survey");
            // return question json object list
            return survey.getJSONArray("questions");
        } catch (IOException ioe) {
            return null;
        } catch (JSONException je) {
            return null;
        }
    }


    public void setLayout(JSONObject ques,int title_id) throws JSONException {
        if (ques == null) return;
        setContentView(R.layout.dynamiclayout);
        ((TextView) findViewById(R.id.title)).setText(title_id);
        ((TextView) findViewById(R.id.tv_question)).setText(ques.getString("question"));
        // get options
        JSONArray jArray = ques.getJSONArray("options");
        int size = jArray.length();
        String[] optionText = new String[size];
        RadioGroup rGroup = findViewById(R.id.rg_options);
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < size; i++) {
            optionText[i] = ((JSONObject) jArray.get(i)).getString(String.valueOf(i + 1));
            RadioButton option = new RadioButton(this);
            option.setText(optionText[i]);
            option.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.font_size_average));
            option.setPadding(0,5,0,5);
            rGroup.addView(option, lp);
        }
    }


    // the app will check whether the user agrees to
    // our requirements or not, then load the question layout
    public void onClickGo(View view) {
        if (checkbox_accept.isChecked()) {
            ToNextPage();
        } else {
            Toast.makeText(this, "Please accept requirements first!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // the app will save the user's answer after clicking
    // the button on the single choice question layout
    public void onClickSingleNext(View view) {
        RadioGroup rGroup = findViewById(R.id.rg_options);
        // get the checked radiobutton
        int checkedId = rGroup.getCheckedRadioButtonId();
        if (checkedId > 0) {
            try{
                String answer = ((RadioButton) findViewById(checkedId)).getText().toString();
                Log.i("onClickSingleNext", answer);
                JSONObject jQuestion = new JSONObject();
                jQuestion.put("type","single");
                TextView question = findViewById(R.id.tv_question);
                jQuestion.put("question", question.getText().toString());
                JSONObject jOption = new JSONObject();
                jOption.put("1",answer);
                jQuestion.put("answer", jOption);
                answers[qSeq-1] = jQuestion;
                ToNextPage(); // load next question
            }catch (JSONException je){
                return;
            }
        }else{
            Toast.makeText(this, "Please select one",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void ToNextPage() {
        try {
            if (qSeq < quest_num) {
                JSONObject question = ((JSONObject) questions.get(qSeq++));
                String type = question.getString("type");
                if (type.equals("single")) {
                    setLayout(question, R.string.single);
                }
            } else {
                setContentView(R.layout.finish_survey);
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }


    //welcome
    public void Click_start(View view) {
        if (checkbox_accept.isChecked()) {
            //setContentView(R.layout.question_one);
            setContentView(R.layout.dynamiclayout);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Please accept these requests first.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    //Question 1
    public void Click_next1(View view) {
        RadioButton rb1, rb2, rb3, rb4, rb5, rb6, rb7;
        rb1 = findViewById(R.id.radioGroup1_1);
        rb2 = findViewById(R.id.radioGroup1_2);
        rb3 = findViewById(R.id.radioGroup1_3);
        rb4 = findViewById(R.id.radioGroup1_4);
        rb5 = findViewById(R.id.radioGroup1_5);
        rb6 = findViewById(R.id.radioGroup1_6);
        rb7 = findViewById(R.id.radioGroup1_7);
        group1 = findViewById(R.id.radioGroup1);
        if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked() || rb5.isChecked() || rb6.isChecked() || rb7.isChecked()) {

            RadioButton a1;
            a1 = findViewById(group1.getCheckedRadioButtonId());
            str1 = a1.getText().toString();
            setContentView(R.layout.question_two);

        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Please select one.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }

    public void Click_next2(View view) {
        RadioButton rb1, rb2, rb3, rb4, rb5;
        group2 = findViewById(R.id.radioGroup2);
        rb1 = findViewById(R.id.radioGroup2_1);
        rb2 = findViewById(R.id.radioGroup2_2);
        rb3 = findViewById(R.id.radioGroup2_3);
        rb4 = findViewById(R.id.radioGroup2_4);
        rb5 = findViewById(R.id.radioGroup2_5);
        if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked() || rb5.isChecked()) {
            RadioButton a2;
            a2 = findViewById(group2.getCheckedRadioButtonId());
            str2 = a2.getText().toString();
            setContentView(R.layout.question_three);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Please select one.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }


    public void Click_next3(View view) {
        RadioButton rb1, rb2, rb3, rb4;
        group3 = findViewById(R.id.radioGroup3);

        rb1 = findViewById(R.id.radioGroup3_1);
        rb2 = findViewById(R.id.radioGroup3_2);
        rb3 = findViewById(R.id.radioGroup3_3);
        rb4 = findViewById(R.id.radioGroup3_4);
        if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()) {
            RadioButton a3;
            a3 = findViewById(group3.getCheckedRadioButtonId());
            str3 = a3.getText().toString();
            setContentView(R.layout.question_four);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Please select one.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }


    public void Click_next4(View view) {
        int i = 0;
        list_q4.add((CheckBox) findViewById(R.id.cb_4_1));
        list_q4.add((CheckBox) findViewById(R.id.cb_4_2));
        list_q4.add((CheckBox) findViewById(R.id.cb_4_3));
        list_q4.add((CheckBox) findViewById(R.id.cb_4_4));
        list_q4.add((CheckBox) findViewById(R.id.cb_4_5));
        list_q4.add((CheckBox) findViewById(R.id.cb_4_6));
        list_q4.add((CheckBox) findViewById(R.id.cb_4_7));
        for (CheckBox checkBox : list_q4) {
            if (checkBox.isChecked()) {
                if (checkBox.getText().toString().indexOf("Bussiness") > -1)
                    checkBox.setText("Bussiness functions");
                if (checkBox.getText().toString().indexOf("Data") > -1)
                    checkBox.setText("Data functions");
                i = 1;
            }
//            if(checkBox.getText().toString().indexOf("Business")>0)checkBox.setText("Business functions");
//            if(checkBox.getText().toString().indexOf("Data")>0)checkBox.setText("Data functions");
        }
        if (i == 0) {
            Toast.makeText(this, "Please choose at least one", Toast.LENGTH_LONG).show();
        } else {
            str4 = "";
            for (CheckBox checkBox : list_q4) {
                if (checkBox.isChecked()) {
                    str4 = str4 + checkBox.getText() + ",";
                }
            }
            setContentView(R.layout.question_five);
        }


    }


    public void Click_next5(View view) {
        int i = 0;
        list_q5.add((CheckBox) findViewById(R.id.cb_5_1));
        list_q5.add((CheckBox) findViewById(R.id.cb_5_2));
        list_q5.add((CheckBox) findViewById(R.id.cb_5_3));
        list_q5.add((CheckBox) findViewById(R.id.cb_5_4));
        list_q5.add((CheckBox) findViewById(R.id.cb_5_5));
        list_q5.add((CheckBox) findViewById(R.id.cb_5_6));
        list_q5.add((CheckBox) findViewById(R.id.cb_5_7));
        for (CheckBox checkBox : list_q5) {
            if (checkBox.isChecked()) {
                if (checkBox.getText().toString().indexOf("Bussiness") > -1)
                    checkBox.setText("Bussiness functions");
                if (checkBox.getText().toString().indexOf("Data") > -1)
                    checkBox.setText("Data functions");
                i = 1;
            }
        }
        if (i == 0) {
            Toast.makeText(this, "Please choose at least one", Toast.LENGTH_LONG).show();
        } else {
            str5 = "";
            for (CheckBox checkBox : list_q5) {
                if (checkBox.isChecked()) {
                    str5 = str5 + checkBox.getText() + ",";
                }
            }
            setContentView(R.layout.question_six);
        }
    }


    public void Click_next6(View view) {
        et6 = (EditText) findViewById(R.id.et_question6);
        str6 = et6.getText().toString();
        setContentView(R.layout.question_seven);
    }


    public void Click_next7(View view) {
        RadioButton rb1, rb2, rb3, rb4, rb5;
        group7 = findViewById(R.id.radioGroup7);

        rb1 = findViewById(R.id.radioGroup7_1);
        rb2 = findViewById(R.id.radioGroup7_2);
        rb3 = findViewById(R.id.radioGroup7_3);
        rb4 = findViewById(R.id.radioGroup7_4);
        rb5 = findViewById(R.id.radioGroup7_5);
        if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked() || rb5.isChecked()) {
            RadioButton a7;
            a7 = findViewById(group7.getCheckedRadioButtonId());
            str7 = a7.getText().toString();
            setContentView(R.layout.question_eight);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Please select one.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }


    public void Click_next8(View view) {
        RadioButton rb1, rb2, rb3, rb4, rb5, rb6, rb7;
        group8 = findViewById(R.id.radioGroup8);

        rb1 = findViewById(R.id.radioGroup8_1);
        rb2 = findViewById(R.id.radioGroup8_2);
        rb3 = findViewById(R.id.radioGroup8_3);
        rb4 = findViewById(R.id.radioGroup8_4);
        rb5 = findViewById(R.id.radioGroup8_5);
        rb6 = findViewById(R.id.radioGroup8_6);
        rb7 = findViewById(R.id.radioGroup8_7);
        if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked() || rb5.isChecked() || rb6.isChecked() || rb7.isChecked()) {
            RadioButton a8;
            a8 = findViewById(group8.getCheckedRadioButtonId());
            str8 = a8.getText().toString();
            setContentView(R.layout.question_nine);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Please select one.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }


    public void Click_next9(View view) {
        RadioButton rb1, rb2, rb3, rb4;
        group9 = findViewById(R.id.radioGroup9);

        rb1 = findViewById(R.id.radioGroup9_1);
        rb2 = findViewById(R.id.radioGroup9_2);
        rb3 = findViewById(R.id.radioGroup9_3);
        rb4 = findViewById(R.id.radioGroup9_4);
        if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()) {
            RadioButton a9;
            a9 = findViewById(group9.getCheckedRadioButtonId());
            str9 = a9.getText().toString();
            setContentView(R.layout.question_ten);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Please select one.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }


    public void Click_next10(View view) {
        RadioButton rb1, rb2, rb3, rb4;
        group10 = findViewById(R.id.radioGroup10);

        rb1 = findViewById(R.id.radioGroup10_1);
        rb2 = findViewById(R.id.radioGroup10_2);
        rb3 = findViewById(R.id.radioGroup10_3);
        rb4 = findViewById(R.id.radioGroup10_4);
        if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()) {
            RadioButton a10;
            a10 = findViewById(group10.getCheckedRadioButtonId());
            str10 = a10.getText().toString();
            setContentView(R.layout.question_eleven);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Please select one.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }


    public void Click_next11(View view) {
        RadioButton rb1, rb2;
        group11 = findViewById(R.id.radioGroup11);

        rb1 = findViewById(R.id.radioGroup11_1);
        rb2 = findViewById(R.id.radioGroup11_2);
        if (rb1.isChecked() || rb2.isChecked()) {
            RadioButton a11;
            a11 = findViewById(group11.getCheckedRadioButtonId());
            str11 = a11.getText().toString();
            setContentView(R.layout.question_twelve);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Please select one.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }


    public void Click_next12(View view) {
        RadioButton rb1, rb2, rb3, rb4, rb5;
        group12 = findViewById(R.id.radioGroup12);
        rb1 = findViewById(R.id.radioGroup12_1);
        rb2 = findViewById(R.id.radioGroup12_2);
        rb3 = findViewById(R.id.radioGroup12_3);
        rb4 = findViewById(R.id.radioGroup12_4);
        rb5 = findViewById(R.id.radioGroup12_5);
        if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked() || rb5.isChecked()) {
            RadioButton a12;
            a12 = findViewById(group12.getCheckedRadioButtonId());
            str12 = a12.getText().toString();
            setContentView(R.layout.finish_survey);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Please select one.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }


    public void Click_finish(View view) throws IOException {
        setContentView(R.layout.report);
        //report
        answer1 = findViewById(R.id.tv_answer1);
        answer2 = findViewById(R.id.tv_answer2);
        answer3 = findViewById(R.id.tv_answer3);
        answer4 = findViewById(R.id.tv_answer4);
        answer5 = findViewById(R.id.tv_answer5);
        answer6 = findViewById(R.id.tv_answer6);
        answer7 = findViewById(R.id.tv_answer7);
        answer8 = findViewById(R.id.tv_answer8);
        answer9 = findViewById(R.id.tv_answer9);
        answer10 = findViewById(R.id.tv_answer10);
        answer11 = findViewById(R.id.tv_answer11);
        answer12 = findViewById(R.id.tv_answer12);

        answer1.setText(str1);
        answer2.setText(str2);
        answer3.setText(str3);
        answer4.setText(str4);
        answer5.setText(str5);
        answer6.setText(str6);
        answer7.setText(str7);
        answer8.setText(str8);
        answer9.setText(str9);
        answer10.setText(str10);
        answer11.setText(str11);
        answer12.setText(str12);

        ToJson();

    }

    public void Click_back(View view) {
        setContentView(R.layout.welcome);
    }


    //string to JSON
    public void ToJson() throws IOException {
        Map<String, String> map1 = new HashMap<String, String>();
        Map<String, String> map2 = new HashMap<String, String>();
        Map<String, String> map3 = new HashMap<String, String>();
        Map<String, String> map4 = new HashMap<String, String>();
        Map<String, String> map5 = new HashMap<String, String>();
        Map<String, String> map6 = new HashMap<String, String>();
        Map<String, String> map7 = new HashMap<String, String>();
        Map<String, String> map8 = new HashMap<String, String>();
        Map<String, String> map9 = new HashMap<String, String>();
        Map<String, String> map10 = new HashMap<String, String>();
        Map<String, String> map11 = new HashMap<String, String>();
        Map<String, String> map12 = new HashMap<String, String>();


        map1.put("Qustion1.want_to_buy", str1);
        map2.put("Qustion2.how much", str2);
        map3.put("Qustion3.the kind of phone", str3);
        map4.put("Qustion4.functions(own)", str4);
        map5.put("Qustion5.the most used functions", str5);
        map6.put("Qustion6.expect functions", str6);
        map7.put("Qustion7.when to a new phone", str7);
        map8.put("Qustion8.want to buy", str8);
        map9.put("Qustion9.the important factory", str9);
        map10.put("Qustion10.age", str10);
        map11.put("Qustion11.gender", str11);
        map12.put("Qustion12.ern money per month", str12);

        List<Map> list = new ArrayList<Map>();
        list.add(map1);
        list.add(map2);
        list.add(map3);
        list.add(map4);
        list.add(map5);
        list.add(map6);
        list.add(map7);
        list.add(map8);
        list.add(map9);
        list.add(map10);
        list.add(map11);
        list.add(map12);


        ja.put(map1);
        ja.put(map2);
        ja.put(map3);
        ja.put(map4);
        ja.put(map5);
        ja.put(map6);
        ja.put(map7);
        ja.put(map8);
        ja.put(map9);
        ja.put(map10);
        ja.put(map11);
        ja.put(map12);

        System.out.println(ja.toString());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(),"Request Writing Permission",Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

        saveDataToFile();
        saveToSdcard();



    }

    //保存数据到文件
    public void saveDataToFile() {
        String filename = "data.txt";
        //String filename="result.json";
        try {
            FileOutputStream outputStream = openFileOutput(filename, Context.MODE_APPEND);
            String data = ja.toString();
            outputStream.write(data.getBytes());
            outputStream.write('\r');
            outputStream.flush();
            outputStream.close();

        } catch (Exception e) {
            Toast.makeText(this, "Saving Error!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }

    public  void  saveToSdcard(){
        FileOutputStream fos = null;
        //获取SD卡状态
        String state = Environment.getExternalStorageState();
        //判断SD卡是否就绪
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "Please check SDcard", Toast.LENGTH_SHORT).show();
            return;
        }
        //取得SD卡根目录
        File file = Environment.getExternalStorageDirectory();
        try {
            Log.d("======SD卡根目录：", "" + file.getCanonicalPath());

            fos = new FileOutputStream(file.getCanonicalPath() + "/sd_data.txt");



            String data=ja.toString();

            fos.write(data.getBytes());
            fos.write('\r');
            Toast.makeText(this, "Save Successfully ", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }


}


