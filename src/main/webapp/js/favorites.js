// Biblioteca de articulos favoritos.
(function(){
  const api=window.JeanPipiApi,ui=window.JeanPipiUI;
  async function load(){const grid=document.getElementById('favoritesGrid'),empty=document.getElementById('favoritesEmpty');ui.skeletonCards(grid,4);try{const items=await api.get('/api/v1/favorites');ui.renderArticles(grid,items);empty.classList.toggle('d-none',items.length>0);}catch(e){if(e.status===403)location.href=api.context+'/login.jsp?next=favoritos.jsp';else{grid.innerHTML='';empty.classList.remove('d-none');}}}load();
})();
