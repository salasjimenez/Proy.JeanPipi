// Listado dinamico de una categoria.
(function(){
  const api=window.JeanPipiApi,ui=window.JeanPipiUI,slug=document.querySelector('meta[name="category-slug"]').content;
  let page=1;
  async function load(target){
    page=target||1;const grid=document.getElementById('categoryArticles'),empty=document.getElementById('categoryEmpty');ui.skeletonCards(grid,8);empty.classList.add('d-none');
    const p=new URLSearchParams({category:slug,page:String(page),size:'12',order:document.getElementById('categoryOrder').value});const q=document.getElementById('categoryQuery').value.trim();if(q)p.set('q',q);
    try{const data=await api.get('/api/v1/articles?'+p.toString());ui.renderArticles(grid,data.items);empty.classList.toggle('d-none',data.items.length>0);ui.pagination(document.getElementById('categoryPagination'),data.page,data.totalPages,load);}catch(e){grid.innerHTML='';empty.classList.remove('d-none');}
  }
  document.getElementById('categoryFilters').addEventListener('submit',e=>{e.preventDefault();load(1);});load(1);
})();
