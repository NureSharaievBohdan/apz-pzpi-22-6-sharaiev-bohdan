FROM node:20

WORKDIR /app

COPY package*.json ./

RUN npm install --legacy-peer-deps


COPY . .

RUN npm run build

RUN npm install -g serve

EXPOSE 3000

CMD ["serve", "-s", "build", "-l", "3000"]
