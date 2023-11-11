package com.raxim.myscoutee.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.Header;

import com.raxim.myscoutee.common.config.properties.MqttProperties;
import com.raxim.myscoutee.profile.handler.MqttMessageHandler;

@Configuration
@EnableIntegration
public class MqttConfig {

    @Autowired
    private MqttProperties mqttProperties;

    @Bean
    public DirectChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInbound() {
        MqttPahoClientFactory mqttPahoClientFactory = new DefaultMqttPahoClientFactory();
        mqttPahoClientFactory.getConnectionOptions().setUserName(mqttProperties.getUsername());
        mqttPahoClientFactory.getConnectionOptions().setServerURIs(new String[] { mqttProperties.getUrl() });
        mqttPahoClientFactory.getConnectionOptions().setCleanSession(true);
        mqttPahoClientFactory.getConnectionOptions().setConnectionTimeout(30);
        mqttPahoClientFactory.getConnectionOptions().setKeepAliveInterval(60);
        mqttPahoClientFactory.getConnectionOptions().setAutomaticReconnect(true);

        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                mqttProperties.getClientId(), mqttPahoClientFactory,
                mqttProperties.getDefaultTopic());

        adapter.setCompletionTimeout(mqttProperties.getTimeout());
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);

        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MqttMessageHandler();
    }

    @Bean
    public DirectChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MqttPahoMessageHandler mqttOutbound() {
        MqttPahoClientFactory mqttPahoClientFactory = new DefaultMqttPahoClientFactory();
        mqttPahoClientFactory.getConnectionOptions().setUserName(mqttProperties.getUsername());
        mqttPahoClientFactory.getConnectionOptions().setServerURIs(new String[] { mqttProperties.getUrl() });

        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(mqttProperties.getUrl(),
                mqttProperties.getClientId(), mqttPahoClientFactory);
        messageHandler.setAsync(true);
        messageHandler.setCompletionTimeout(mqttProperties.getTimeout());
        messageHandler.setDefaultTopic(mqttProperties.getDefaultTopic());
        return messageHandler;
    }

    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MqttGateway {
        void sendToMqtt(String data);

        void sendToMqtt(@Header("mqtt_topic") String topic, String data);
    }
}
