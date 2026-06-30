#!/bin/bash
DOMAINS="user course post proposal exchange chat notification report graduation"
SUBDIRS="controller service repository entity dto/request dto/response"

for d in $DOMAINS; do
  for s in $SUBDIRS; do
    mkdir -p "domain/$d/$s"
    touch "domain/$d/$s/.gitkeep"
  done
done

mkdir -p infra/s3 infra/discord
touch infra/s3/.gitkeep
touch infra/discord/.gitkeep

echo "완료!"
