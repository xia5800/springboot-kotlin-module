#!/bin/bash
docker-compose down
docker rmi zeta_job_server
docker-compose up -d
