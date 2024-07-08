import { DataTypes } from 'sequelize';
import { sequelize } from '#Config/db.js';

const UserModel = sequelize.define('Usuario', {
    id: {
        type: DataTypes.INTEGER,
        autoIncrement: true,
        primaryKey: true,
    },
    nombre: {
        type: DataTypes.STRING,
        allowNull: false,
        validate: {
            len: [1, 50],
        },
    },
    apellido: {
        type: DataTypes.STRING,
        allowNull: false,
        validate: {
            len: [1, 50],
        },
    },
    dni: {
        type: DataTypes.STRING,
        allowNull: false,
        validate: {
            len: [1, 50],
        },
    },
    email: {
        type: DataTypes.STRING,
        allowNull: false,
        unique: true,
    },
    password: {
        type: DataTypes.STRING,
        allowNull: false,
        validate: {
            len: [8, 50],
        },
    },
    rol: {
        type: DataTypes.INTEGER,
        allowNull: false,
        defaultValue: 0,
    },
});

ModeloUsuario.sync({ alter: true })
    .then(() => console.log('User table synced'))
    .catch((error) => console.error('Error syncing User table:', error));

export default ModeloUsuario;