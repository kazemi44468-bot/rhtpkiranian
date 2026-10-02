const http=require("http");
const fs=require("fs");
const path=require("path");
const crypto=require("crypto");
const PORT=Number(process.env.PORT||8080),HOST=process.env.HOST||"0.0.0.0";
const DATA_DIR=path.join(__dirname,"data"),DATA_FILE=path.join(DATA_DIR,"records.json");
const seed=[
{id:"REQ-1405-001",title:"تأمین تجهیزات پروژه توسعه",kind:"request",status:"در حال بررسی",owner:"واحد تأمین",priority:"فوری",done:false},
{id:"SUP-1405-007",title:"شبکه تأمین تجهیزات صنعتی",kind:"supplier",status:"تکمیل اطلاعات",owner:"مرکز عملیات",priority:"عادی",done:false},
{id:"PRJ-1405-003",title:"فرصت پروژه زیرساخت هوشمند",kind:"project",status:"تأیید / آماده اجرا",owner:"مدیریت پروژه",priority:"فوری",done:false},
{id:"REQ-1405-002",title:"درخواست خدمات پشتیبانی",kind:"request",status:"مختومه",owner:"واحد خدمات",priority:"عادی",done:true}
];
function ensure(){if(!fs.existsSync(DATA_DIR))fs.mkdirSync(DATA_DIR,{recursive:true});if(!fs.existsSync(DATA_FILE))fs.writeFileSync(DATA_FILE,JSON.stringify(seed,null,2),"utf8")}
function read(){ensure();return JSON.parse(fs.readFileSync(DATA_FILE,"utf8"))}
function write(x){ensure();fs.writeFileSync(DATA_FILE,JSON.stringify(x,null,2),"utf8")}
function json(res,status,data){const b=JSON.stringify(data);res.writeHead(status,{"Content-Type":"application/json; charset=utf-8","Access-Control-Allow-Origin":"*","Access-Control-Allow-Headers":"Content-Type, Authorization","Access-Control-Allow-Methods":"GET,POST,PATCH,OPTIONS"});res.end(b)}
function body(req){return new Promise((resolve,reject)=>{let raw="";req.on("data",c=>raw+=c);req.on("end",()=>{try{resolve(raw?JSON.parse(raw):{})}catch(e){reject(e)}});req.on("error",reject)})}
function newid(){return "REC-"+Date.now().toString(36).toUpperCase()+"-"+crypto.randomBytes(2).toString("hex").toUpperCase()}
const server=http.createServer(async(req,res)=>{
if(req.method==="OPTIONS"){res.writeHead(204,{"Access-Control-Allow-Origin":"*","Access-Control-Allow-Headers":"Content-Type, Authorization","Access-Control-Allow-Methods":"GET,POST,PATCH,OPTIONS"});return res.end()}
const u=new URL(req.url,"http://localhost"),p=u.pathname.split("/").filter(Boolean);
try{
if(req.method==="GET"&&u.pathname==="/api/v1/health")return json(res,200,{ok:true,service:"rhtpkiranian-api",version:"1.0.0",time:new Date().toISOString()});
if(req.method==="GET"&&u.pathname==="/api/v1/dashboard"){const r=read(),o=r.filter(x=>!x.done);return json(res,200,{ok:true,metrics:{open:o.length,urgent:r.filter(x=>x.priority==="فوری").length,suppliers:28,projects:8},queue:o.slice(-5).reverse(),composition:{request:r.filter(x=>x.kind==="request").length,supplier:r.filter(x=>x.kind==="supplier").length,project:r.filter(x=>x.kind==="project").length}})}
if(req.method==="GET"&&u.pathname==="/api/v1/records")return json(res,200,{ok:true,data:read()});
if(req.method==="POST"&&u.pathname==="/api/v1/records"){const x=await body(req);if(!x.title)return json(res,400,{ok:false,error:"title is required"});const r={id:newid(),title:String(x.title),kind:x.kind||"request",status:x.status||"ثبت اولیه",owner:x.owner||"در انتظار ارجاع",priority:x.priority||"عادی",done:Boolean(x.done)};const a=read();a.push(r);write(a);return json(res,201,{ok:true,data:r})}
if(p[0]==="api"&&p[1]==="v1"&&p[2]==="records"&&p[3]&&req.method==="GET"){const r=read().find(x=>x.id===p[3]);return r?json(res,200,{ok:true,data:r}):json(res,404,{ok:false,error:"record not found"})}
if(p[0]==="api"&&p[1]==="v1"&&p[2]==="records"&&p[3]&&req.method==="PATCH"){const a=read(),i=a.findIndex(x=>x.id===p[3]);if(i<0)return json(res,404,{ok:false,error:"record not found"});a[i]={...a[i],...(await body(req)),id:a[i].id};write(a);return json(res,200,{ok:true,data:a[i]})}
if(req.method==="GET"&&u.pathname==="/api/v1/suppliers")return json(res,200,{ok:true,count:28,data:read().filter(x=>x.kind==="supplier")});
if(req.method==="GET"&&u.pathname==="/api/v1/projects")return json(res,200,{ok:true,count:8,data:read().filter(x=>x.kind==="project")});
if(req.method==="GET"&&u.pathname==="/api/v1/reports"){const r=read();return json(res,200,{ok:true,summary:{total:r.length,open:r.filter(x=>!x.done).length,closed:r.filter(x=>x.done).length},byKind:{request:r.filter(x=>x.kind==="request").length,supplier:r.filter(x=>x.kind==="supplier").length,project:r.filter(x=>x.kind==="project").length}})}
return json(res,404,{ok:false,error:"route not found"})
}catch(e){console.error(e);return json(res,500,{ok:false,error:"server error"})}});
server.listen(PORT,HOST,()=>console.log("RHTP Iranian API listening on "+PORT));
