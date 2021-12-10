'''
 * Trabalho Final
 *          SSC0147 - Tópicos Especiais em Sistemas de Computação I
 * 
 * Docente: 
 *          Jo Ueyama
 * 
 * Alunos:
 *          Marilene Andrade Garcia - 10276974
 *          Vinícius L. S. Genesio  - 10284688
 *          
 * Código baseado em:
 * https://www.emqx.com/en/blog/how-to-use-mqtt-in-python
'''

import random
import time
from paho.mqtt import client as mqtt_client

broker = 'broker.emqx.io'
port = 1883
topic = "ssc0147/trab_final/sensor"
# conversar do sensor
client_id = 'mari-pc'
username = 'emqx'
password = 'public'
indice_anterior = -1
indice = -1
blockchain = []

def connect_mqtt():
    def on_connect(client, userdata, flags, rc):
        if rc == 0:
            print("Connected to MQTT Broker!")
        else:
            print("Failed to connect, return code %d\n", rc)

    client = mqtt_client.Client(client_id)
    client.username_pw_set(username, password)
    client.on_connect = on_connect
    client.connect(broker, port)
    return client


def subscribe(client: mqtt_client):
    def on_message(client, userdata, msg):  
        blockchain_simplificada(msg.payload.decode()) 
        # Enviando para o celular
        topic = "ssc0147/trab_final"
        client.publish(topic, msg.payload.decode())     
        # Novamente receber do sensor
        topic = "ssc0147/trab_final/sensor"

    client.subscribe(topic)
    client.on_message = on_message


def blockchain_simplificada(mensagem):
    msg = mensagem.split(' ')
    global indice_anterior
    global indice
    global blockchain 

    if (float(msg[3]) == -1.0):
        indice_anterior = float(msg[3])
        indice = float(msg[2])

        leituras = (msg[0], msg[1])
        bloco = []
        bloco.append(indice)
        bloco.append(leituras)
        
        blockchain.append(bloco)

    elif (float(msg[3]) == float(indice)):
        indice_anterior = float(msg[3])
        indice = msg[2]

        leituras = (msg[0], msg[1])
        bloco = []
        bloco.append(indice)
        bloco.append(leituras)

        blockchain.append(bloco)
    else:
        indice_anterior = float(msg[3])
        indice = float(msg[2])

        leituras = (msg[0], msg[1])
        bloco = []
        bloco.append(indice)
        bloco.append(leituras)
        
        blockchain.append(bloco)

        ''' Teria que ser essa linha '''
        #raise Exception("Valores inválidos foram recebidos")
    
    print(blockchain)
        

        

def run():
    client = connect_mqtt()
    subscribe(client)
    client.loop_forever()

if __name__ == '__main__':
    run()

