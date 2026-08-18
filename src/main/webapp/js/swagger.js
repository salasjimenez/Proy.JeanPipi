// Inicializacion de Swagger UI.
window.addEventListener('load',function(){
  const context=document.querySelector('meta[name="app-context"]')?.content||'';
  window.SwaggerUIBundle({url:context+'/openapi.yaml',dom_id:'#swagger-ui',deepLinking:true});
});
