package com.example.iot_th;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import javax.security.auth.callback.CallbackHandler;


public class MainActivity extends AppCompatActivity {
    private Button botao;
    private Button botao2;
    private TextView textView;
    private TextView textView2;
    private final String USERNAME = "emqx";
    private final String PASSWORD = "public";
    private String topic = "ssc0147/trab_final";
    private byte[] texto = "teste".getBytes();
    private MqttAndroidClient client;
    String[] medidas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Conectar com o broker mqqt
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.emqx.io:1883", clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            IMqttToken token = client.connect(options);
            if(!client.isConnected()){
                Log.i("ERROR", "Cliente não conectado");
            }

            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("TAG", "onSuccess");

                    textView = (TextView) findViewById(R.id.textView2);
                    textView2 = (TextView) findViewById(R.id.textView3);
                    botao = findViewById(R.id.botao);
                    botao2 = findViewById(R.id.botao2);


                    try {
                        client.subscribe(topic, 1, null, new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                Log.d("LOG_TAG", "Successfully subscribed to topic.");
                                client.setCallback(new MqttCallback() {
                                    @Override
                                    public void connectionLost(Throwable cause) {
                                    }

                                    @Override
                                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                                        medidas = new String(message.getPayload()).split(" ");
                                        textView.setText(medidas[0]+" ºC");
                                        textView2.setText(medidas[1]+"  %");

                                    }

                                    @Override
                                    public void deliveryComplete(IMqttDeliveryToken token) {

                                    }
                                });
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                Log.d("LOG_TAG", "Failed to subscribed to topic.");
                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }



                    //MqttMessage message = new MqttMessage(texto);
                    //client.subscribe(topic, 1, texto);
                    /*botao2.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     texto = mensagem.getText().toString().getBytes();

                                                     if(!texto.toString().isEmpty()){
                                                         try {
                                                             //encodedPayload = texto.getBytes("UTF-8");
                                                             MqttMessage message = new MqttMessage(texto);
                                                             client.publish(topic, message);
                                                         } catch (MqttException e) {
                                                             e.printStackTrace();
                                                         }

                                                         Toast.makeText(MainActivity.this, "Mensagem enviada com sucesso!", Toast.LENGTH_SHORT).show();

                                                     }
                                                     else{
                                                         Toast.makeText(MainActivity.this, "Preencha algum texto.", Toast.LENGTH_SHORT).show();
                                                     }


                                                 }
                                             }
                    );*/
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("TAG", "onFailure");
                    Toast.makeText(MainActivity.this, "Não foi possível fazer a conexão MQTT.", Toast.LENGTH_SHORT).show();

                    finish();

                }
            });




        } catch (MqttException e) {
            e.printStackTrace();
        }


    }
}




