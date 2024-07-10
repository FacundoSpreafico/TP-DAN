import Express from 'express';

import { connectDB } from '#Config/database.js';
import ModeloUsuario from './schema/schemaUsuario.js';
import path from 'path';
import { fileURLToPath } from 'url';
const __dirname = path.dirname(fileURLToPath(import.meta.url));
import { metodologin as loginmethod } from './controllers/user-login-ctrl.js';
//server

const app = Express();

app.set("port",8080)
app.listen(app.get("port"));

//config (hay que pasarla a otro archivo)
app.use(Express.json());

//routes
app.get("/register",(req,res)=> res.sendFile(__dirname + "/pages/register.html"));
//app.get("/register",(req,res)=> res.sendFile(__dirname + "/pages/register.html"));
app.get("/login",(req,res)=> res.sendFile(__dirname + "/pages/login.html"));
app.post("/api/login", loginmethod );
//app.post("/api/register", metodoregister);

//static filess
const start = async () => {
    try {
        await connectDB();
        app.listen(process.env.PORT, () => {
            console.log(`Server running on port ${process.env.PORT}`);
        });
        ModeloUsuario.sync({ alter: true });
        
    } catch (error) {
        console.error('Error starting server:', error);
    }
}

start();