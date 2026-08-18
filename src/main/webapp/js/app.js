// Portada dinamica de JeanPipi.
(function(){
  const api=window.JeanPipiApi,ui=window.JeanPipiUI;
  let page=1;
  let settings={};
  const params=new URLSearchParams(window.location.search);
  const q=params.get('q')||'';
  const query=document.getElementById('filterQuery');if(query)query.value=q;
  async function loadSettings(){
    try{
      const items=await api.get('/api/v1/dashboard/sections');
      items.forEach(s=>{settings[s.clave]=s;});
      const links={destacado:['heroSection','heroTitle'],tendencias:['trendingSection','trendTitle'],ultimos:['latestSection','latestTitle']};
      Object.entries(links).forEach(([key,ids])=>{const config=settings[key],section=document.getElementById(ids[0]),title=document.getElementById(ids[1]);if(!config)return;if(title&&key!=='destacado')title.textContent=config.titulo;if(section)section.classList.toggle('d-none',!config.habilitada);});
      const main=document.querySelector('main');
      items.slice().sort((a,b)=>a.posicion-b.posicion).forEach(s=>{const id=links[s.clave]?.[0],section=id?document.getElementById(id):null;if(section&&main)main.appendChild(section);});
    }catch(e){}
  }
  async function loadHero(){
    if(settings.destacado&&settings.destacado.habilitada===false)return;
    const skeleton=document.getElementById('heroSkeleton'),hero=document.getElementById('heroArticle');
    try{
      let items=await api.get('/api/v1/articles/featured?limit=1');
      if(!items.length){const latest=await api.get('/api/v1/articles?size=1&page=1');items=latest.items;}
      if(!items.length){skeleton.classList.add('d-none');return;}
      const a=items[0];document.getElementById('heroImage').src=ui.safeImage(a.portadaUrl);document.getElementById('heroImage').alt=a.titulo;
      document.getElementById('heroCategory').textContent=a.categoriaNombre||'Editorial';document.getElementById('heroTitle').textContent=a.titulo;document.getElementById('heroExcerpt').textContent=a.extracto||'';document.getElementById('heroLink').href=api.context+'/articulo/'+encodeURIComponent(a.slug);
      skeleton.classList.add('d-none');hero.classList.remove('d-none');
    }catch(e){skeleton.classList.add('d-none');}
  }
  async function loadTrending(){
    if(settings.tendencias&&settings.tendencias.habilitada===false)return;
    const grid=document.getElementById('trendingGrid'),limit=settings.tendencias?.limiteItems||6;ui.skeletonCards(grid,Math.min(limit,6));
    try{ui.renderArticles(grid,await api.get('/api/v1/articles/trending?limit='+limit));}catch(e){grid.innerHTML='';}
  }
  async function loadArticles(targetPage){
    if(settings.ultimos&&settings.ultimos.habilitada===false)return;
    page=targetPage||1;const grid=document.getElementById('articlesGrid'),skeleton=document.getElementById('articlesSkeleton'),empty=document.getElementById('articlesEmpty');
    grid.innerHTML='';empty.classList.add('d-none');ui.skeletonCards(skeleton,8);
    const pageSize=settings.ultimos?.limiteItems||12;
    const search=new URLSearchParams({page:String(page),size:String(pageSize),order:document.getElementById('filterOrder').value});
    const values={q:document.getElementById('filterQuery').value.trim(),author:document.getElementById('filterAuthor').value.trim(),category:document.getElementById('filterCategory').value};
    Object.entries(values).forEach(([k,v])=>{if(v)search.set(k,v);});
    try{
      const data=await api.get('/api/v1/articles?'+search.toString());skeleton.innerHTML='';ui.renderArticles(grid,data.items);empty.classList.toggle('d-none',data.items.length>0);ui.pagination(document.getElementById('pagination'),data.page,data.totalPages,loadArticles);
    }catch(e){skeleton.innerHTML='';empty.classList.remove('d-none');empty.querySelector('p').textContent='No se pudo cargar el contenido.';}
  }
  document.getElementById('articleFilters').addEventListener('submit',e=>{e.preventDefault();loadArticles(1);});
  async function start(){await loadSettings();await Promise.allSettled([loadHero(),loadTrending(),loadArticles(1)]);}
  start();
})();
