FROM nginx:1.17.4-alpine
ARG APP_VERSION
ENV APP_VERSION=${APP_VERSION:-v1.0.0}
RUN rm -rf /usr/share/nginx/html
COPY ./dist /usr/share/nginx/html

