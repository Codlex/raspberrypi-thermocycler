
echo "Updating jar..."
rm -f Thermocycler.jar
wget https://raw.githubusercontent.com/Codlex/raspberrypi-thermocycler/master/builds/3.0/Thermocycler.jar
chmod +x Thermocycler.jar
echo "Jar updated!"

echo "Updating settings..."
rm -f settings.properties
wget https://raw.githubusercontent.com/Codlex/raspberrypi-thermocycler/master/builds/3.0/settings.properties
echo "Settings updated!"

echo "Updating start script..."
rm -f start.sh
wget https://raw.githubusercontent.com/Codlex/raspberrypi-thermocycler/master/builds/3.0/start.sh
chmod +x start.sh
echo "Start script updated!"

echo "Updating log4..."
rm -f log4j.properties
wget https://raw.githubusercontent.com/Codlex/raspberrypi-thermocycler/master/builds/3.0/log4j.properties
echo "Log4 updated!"

mkdir log

echo "Thermocycler update complete!"
