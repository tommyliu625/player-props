#!/bin/bash
echo 'Post deploy executing'
aws configure set region us-east-1
cd /var/app/current
echo "Hello from AWS hook v5" >> 'test.txt'
sudo su
sudo chmod -R 777 /var/my_app
rm -rf /var/my_app
mkdir /var/my_app
aws s3 sync s3://player-props-client /var/my_app
sudo chmod -R 755 /var/my_app
echo 'Post deploy executed'