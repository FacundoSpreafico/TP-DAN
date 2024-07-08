import Express from 'express';

import path from 'path';
import { fileURLToPath } from 'url';
const __dirname = path.dirname(fileURLToPath(import.meta.url));
//server

const app = Express();

app.set("port",8080)
app.listen(app.get("port"));
console.log("Server on port",app.get("port"));


//
app.get("/",(req,res)=> res.sendFile(__dirname + "/pages/login.html"));{

}
