// Cliente HTTP de JeanPipi.
(function(){
  const meta=document.querySelector('meta[name="app-context"]');
  const context=meta?meta.content:'';
  let csrfToken=null;
  async function ensureCsrf(){
    if(csrfToken)return csrfToken;
    const response=await fetch(context+'/api/v1/auth/csrf',{credentials:'same-origin',headers:{'Accept':'application/json'}});
    const body=await response.json();
    if(!response.ok||!body.ok)throw new Error(body.message||'No se pudo iniciar la sesion segura');
    csrfToken=body.data.csrfToken;
    return csrfToken;
  }
  async function request(path,options){
    const config=Object.assign({method:'GET',credentials:'same-origin',headers:{'Accept':'application/json'}},options||{});
    config.headers=Object.assign({'Accept':'application/json'},config.headers||{});
    const method=(config.method||'GET').toUpperCase();
    if(['POST','PUT','PATCH','DELETE'].includes(method))config.headers['X-CSRF-Token']=await ensureCsrf();
    if(config.body&&!(config.body instanceof FormData)&&typeof config.body!=='string'){
      config.headers['Content-Type']='application/json';
      config.body=JSON.stringify(config.body);
    }
    const response=await fetch(context+path,config);
    let body=null;
    try{body=await response.json();}catch(e){body={ok:false,message:'Respuesta invalida del servidor'};}
    if(!response.ok||!body.ok){
      const error=new Error(body.message||'No se pudo completar la solicitud');
      error.status=response.status;
      throw error;
    }
    return body.data;
  }
  window.JeanPipiApi={context,get:(p)=>request(p),post:(p,b)=>request(p,{method:'POST',body:b}),put:(p,b)=>request(p,{method:'PUT',body:b}),patch:(p,b)=>request(p,{method:'PATCH',body:b}),delete:(p)=>request(p,{method:'DELETE'}),request,ensureCsrf,setCsrf:(value)=>{csrfToken=value;}};
})();
