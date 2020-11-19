var awsIot = require("aws-iot-device-sdk");
var path = require("path");
var mqtt = require("mqtt");

// LIVE CONFIG
var device = awsIot.device({
  keyPath: path.join(__dirname, "/certs/livespprivate.pem.key"),
  certPath: path.join(__dirname, "/certs/livespcertificate.pem.crt"),
  caPath: path.join(__dirname, "/certs/livespca1.pem"),
  clientId: "TEST",
  host: "a2vs8z4dhndn7y-ats.iot.eu-west-1.amazonaws.com",
  protocol: "mqtts",
  keepAlive: 0,
});
var topic = "LIVESPRSP";

device.on("connect", () => {
    console.log("Mqtt connected");
    device.subscribe(topic);
  });
  
  device.on("reset", () => {
    console.log("Mqtt reset");
  });
  
  device.on("close", () => {
    console.log("Connection to MQTT closed");
  });
  
  device.on("reconnect", () => {
    console.log("Mqtt reconnect");
  });
  
  device.on("offline", function () {
    console.log("Mqtt offline");
  });
  
  device.on("error", (error) => {
    console.log(error);
  });
  
  module.exports = {
    device: device,
  };