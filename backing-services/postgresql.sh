#!/bin/bash

set -e

docker run -it \
    -p 5432:5432 \
    -e POSTGRES_USER=batch \
    -e POSTGRES_PASSWORD=batch \
    --name batch-db \
    -d postgres

echo "PostgreSQL running on localhost:5432"
echo "PostgreSQL username: batch"
echo "PostgreSQL password: batch"
echo "PostgreSQL database: batch"
