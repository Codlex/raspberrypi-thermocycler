sudo service lightdm stop &
# sudo java -jar -Dcom.sun.javafx.touch=true -Djavafx.platform=eglfb -Dcom.sun.javafx.isEmbedded=false Test.jar &
sudo java -jar test.jar &> log/startup.log &
