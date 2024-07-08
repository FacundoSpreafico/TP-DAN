# Etapa 1: Construcción de dependencias
FROM node:20-bullseye AS dependencies
WORKDIR /app

# Copiar package.json y package-lock.json para aprovechar la caché de dependencias
COPY package*.json ./

# Instalar las dependencias, incluyendo cors
RUN npm install --force

# Etapa 2: Construcción de la aplicación
FROM node:20-bullseye
WORKDIR /app

# Copiar todos los archivos de la aplicación
COPY . .

# Eliminar node_modules y reinstalar dependencias para asegurar compatibilidad con Linux
RUN rm -rf node_modules && npm install --force

# Exponer el puerto en el que la aplicación va a escuchar
EXPOSE 8081

# Comando para iniciar la aplicación
CMD ["npm", "start"]