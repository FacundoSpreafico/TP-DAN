import { Sequelize } from 'sequelize';
import { config } from 'dotenv';

config();

const sequelize = new Sequelize(
    process.env.PG_DATABASE,
    process.env.PG_USER,
    process.env.PG_PASSWORD,
    {
        host: process.env.PG_HOST,
        port: process.env.PG_PORT,
        dialect: 'postgres'
    }
);

const connectDB = async () => {
    let retries = 10;
    while (retries) {
        try {
            await sequelize.authenticate();
            console.log('Database connected');
            break; // Salir del bucle si la conexión tiene éxito
        }
        catch (error) {
            console.error('Unable to connect to the database:', error);
            retries -= 1;
            console.log(`Retries left: ${retries}`);
            await new Promise((resolve) => setTimeout(resolve, 10000)); // Esperar 5 segundos antes de intentar nuevamente
        }
    }
};


export { sequelize, connectDB };
