#!/bin/bash

# Blue 를 기준으로 현재 떠있는 컨테이너를 체크한다.
EXIST_BLUE=$(sudo docker-compose -p web-blue -f docker-compose-blue.yml ps | grep Up)
echo "EXIST_BLUE value is: $EXIST_BLUE"

if [ -z "$EXIST_BLUE" ]; then
    echo "blue up"
    sudo docker-compose -p web-blue -f docker-compose-blue.yml up --build -d
    BEFORE_COMPOSE_COLOR="green"
    AFTER_COMPOSE_COLOR="blue"
else
    echo "green up"
    sudo docker-compose -p web-green -f docker-compose-green.yml up --build -d
    BEFORE_COMPOSE_COLOR="blue"
    AFTER_COMPOSE_COLOR="green"
fi

sleep 10

# 새로운 컨테이너가 제대로 떴는지 확인
EXIST_AFTER=$(sudo docker-compose -p web-${AFTER_COMPOSE_COLOR} -f docker-compose-${AFTER_COMPOSE_COLOR}.yml ps | grep Up)
if [ -n "$EXIST_AFTER" ]; then
    # nginx.config를 컨테이너에 맞게 변경해주고 reload 한다 -> 필요없을듯?
    sudo cp ./config/nginx/nginx.${AFTER_COMPOSE_COLOR}.conf /etc/nginx/nginx.conf
    sudo nginx -s reload
    # 이전 컨테이너 종료
    sudo docker-compose -p web-${BEFORE_COMPOSE_COLOR} -f docker-compose-${BEFORE_COMPOSE_COLOR}.yml down
    echo "$BEFORE_COMPOSE_COLOR down"
fi
