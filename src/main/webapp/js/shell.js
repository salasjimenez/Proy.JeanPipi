// Comportamiento comun del sitio.
(function(){
  const api=window.JeanPipiApi;
  async function loadCategories(){
    const nav=document.getElementById('categoryNav');
    if(!nav)return;
    try{
      const categories=await api.get('/api/v1/categories');
      nav.innerHTML='';
      categories.forEach(category=>{const a=document.createElement('a');a.href=api.context+'/categoria/'+encodeURIComponent(category.slug);a.textContent=category.nombre;nav.appendChild(a);});
      const selects=[document.getElementById('filterCategory')];
      selects.filter(Boolean).forEach(select=>categories.forEach(category=>{const o=document.createElement('option');o.value=category.slug;o.textContent=category.nombre;select.appendChild(o);}));
    }catch(e){nav.innerHTML='<span class="nav-loading">Categorias no disponibles</span>';}
  }
  function bindSearch(){
    const form=document.getElementById('globalSearchForm');
    if(!form)return;
    form.addEventListener('submit',event=>{event.preventDefault();const value=document.getElementById('globalSearch').value.trim();window.location.href=api.context+'/index.jsp'+(value?'?q='+encodeURIComponent(value):'');});
  }
  function bindLogout(){
    const button=document.getElementById('logoutButton');
    if(button)button.addEventListener('click',async()=>{try{await api.post('/api/v1/auth/logout',{});}finally{window.location.href=api.context+'/index.jsp';}});
  }
  loadCategories();bindSearch();bindLogout();
})();
