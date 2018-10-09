package ir.sana24.miningservice;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SendEvents {

    private Context context;
    private String description;

    public void sendClicked(String description, Context context){
        this.context = context;
        this.description = description;
        new SendMessage().execute();
    }

    private class SendMessage extends AsyncTask<Integer, Integer, Integer> {

        private InputStream is = null;
        private String url;
        String page_output, message;
        Integer result = 1;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            url = "http://saynaair.ir/movahedian/chatroom/sendMessage.php";
        }

        @Override
        protected Integer doInBackground(Integer... args) {
            try {
                // Building Parameters
                List<NameValuePair> nameValuePairs = new ArrayList<>(5);
                nameValuePairs.add(new BasicNameValuePair("title","new message"));
                nameValuePairs.add(new BasicNameValuePair("message",description));
                nameValuePairs.add(new BasicNameValuePair("id","18"));
                nameValuePairs.add(new BasicNameValuePair("to","11"));
                nameValuePairs.add(new BasicNameValuePair("conversation_id","14"));
                // sending params and wait for result and get the result
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try { //converting InputStream to string
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();

                if (sb.length() > 0) {
                    page_output = sb.toString().trim();
                }

                message = "خطا در دریافت اطلاعات";
                if (!page_output.contains("success")) {
                    if (page_output.contains("message_id"))
                        result = 0;
                    else
                        result = 1;
                } else {
                    JSONObject jsonObject = new JSONObject(page_output);
                    if (jsonObject.getInt("success")== 1 ){
                        result = 0;
                    }else{
                        message = jsonObject.getString("message");
                        result = 1;
                    }
                }
            } catch (Exception e) {
                result= 1;
                message = e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer a) {
            if (a == 1) {
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context,"message has sent",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
