FROM node:10.17.0

COPY sources.list /etc/apt/sources.list

RUN apt-get update
RUN apt-get install -y --allow-unauthenticated --allow-remove-essential libaio-dev apt-utils 

# Home directory for Node-RED application source code.
RUN mkdir -p /usr/src/node-red

# User data directory, contains flows, config and nodes.
RUN mkdir /data

RUN mkdir -p /home/hollysys
COPY node-red-contrib-hollicube /home/hollysys/node-red-contrib-hollicube
COPY node-red-contrib-oracledb /home/hollysys/node-red-contrib-oracledb

WORKDIR /usr/src/node-red

# Add node-red user so we aren't running as root.
RUN useradd --home-dir /usr/src/node-red --no-create-home node-red \
    && chown -R node-red:node-red /data \
    && chown -R node-red:node-red /usr/src/node-red \
    && chown -R node-red:node-red /home/hollysys

USER node-red

# package.json contains Node-RED NPM module and node dependencies
COPY package.json /usr/src/node-red/
RUN npm config set registry "http://registry.npm.taobao.org/"
RUN npm install --registry=https://registry.npm.taobao.org/

RUN npm install node-red-contrib-mssql
RUN npm install node-red-contrib-credentials
RUN npm install node-red-node-mysql
RUN npm install node-red-contrib-soap
RUN npm install node-red-contrib-time-range-switch
RUN npm install node-red-contrib-rdkafka

RUN npm install /home/hollysys/node-red-contrib-hollicube

ADD oracle /opt/oracle
ENV LD_LIBRARY_PATH=/opt/oracle/instantclient:$LD_LIBRARY_PATH
ENV OCI_LIB_DIR=/opt/oracle/instantclient
ENV OCI_INC_DIR=/opt/oracle/instantclient/sdk/include
RUN npm install oracledb
RUN npm install /home/hollysys/node-red-contrib-oracledb

# User configuration directory volume
EXPOSE 1880

# Environment variable holding file path for flows configuration
ENV FLOWS=flows.json
ENV NODE_PATH=/usr/src/node-red/node_modules:/data/node_modules
# Add a healthcheck (default every 30 secs)
HEALTHCHECK CMD curl http://localhost:1880/ || exit 1

CMD ["npm", "start", "--", "--userDir", "/data"]
