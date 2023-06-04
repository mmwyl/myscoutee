FROM nginx:1.21.0

RUN apt-get update &&\
    apt-get install -y cron nano curl &&\
    apt-get clean

#wait
COPY wait-for-it.sh /scripts/wait-for-it.sh
RUN chmod +x /scripts/wait-for-it.sh

COPY wait-cmd.sh /scripts/wait-cmd.sh
RUN chmod +x /scripts/wait-cmd.sh

#letsencrypt
#RUN curl -L https://raw.githubusercontent.com/wmnnd/nginx-certbot/master/init-letsencrypt.sh > /scripts/init-letsencrypt.sh
#RUN chmod +x /scripts/init-letsencrypt.sh

CMD ["bash", "-c", "/scripts/wait-cmd.sh"]