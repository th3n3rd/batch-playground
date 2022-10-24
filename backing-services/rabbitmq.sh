#!/bin/bash

set -e

docker run -it \
    -p 15672:15672 \
    -p 5672:5672 \
    --name batch-mq \
    -d rabbitmq:3-management

echo "RabbitMQ console available at: http://localhost:15672"
echo "RabbitMQ username: guest"
echo "RabbitMQ password: guest"
