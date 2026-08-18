// Componentes reutilizables de la interfaz.
(function(){
  const api=window.JeanPipiApi;
  function safeImage(url){
    if(!url)return api.context+'/img/JeanPipi.svg';
    try{
      if(url.startsWith('/'))return url.startsWith(api.context+'/')?url:api.context+url;
      const parsed=new URL(url,window.location.origin);
      if(['http:','https:'].includes(parsed.protocol))return parsed.href;
    }catch(e){}
    return api.context+'/img/JeanPipi.svg';
  }
  function formatDate(value){
    if(!value)return '';
    const d=new Date(value);
    return Number.isNaN(d.getTime())?'':new Intl.DateTimeFormat('es-PE',{year:'numeric',month:'short',day:'2-digit'}).format(d);
  }
  function articleCard(article){
    const card=document.createElement('article');card.className='article-card';
    const link=document.createElement('a');link.href=api.context+'/articulo/'+encodeURIComponent(article.slug);link.setAttribute('aria-label','Leer '+article.titulo);
    const media=document.createElement('div');media.className='article-card-media';
    const img=document.createElement('img');img.src=safeImage(article.portadaUrl);img.alt=article.titulo||'Articulo JeanPipi';img.loading='lazy';media.appendChild(img);
    const category=document.createElement('span');category.className='article-card-category';category.textContent=article.categoriaNombre||'Editorial';
    const title=document.createElement('h3');title.textContent=article.titulo||'Sin titulo';
    const excerpt=document.createElement('p');excerpt.textContent=article.extracto||'';
    const meta=document.createElement('div');meta.className='article-card-meta';
    const author=document.createElement('span');author.textContent=article.autorNombre||'JeanPipi';
    const reading=document.createElement('span');reading.textContent=(article.minutosLectura||1)+' min';
    const date=document.createElement('span');date.textContent=formatDate(article.publicadoAt||article.createdAt);
    meta.append(author,reading,date);link.append(media,category,title,excerpt,meta);card.appendChild(link);return card;
  }
  function renderArticles(container,items){container.innerHTML='';(items||[]).forEach(item=>container.appendChild(articleCard(item)));}
  function pagination(container,page,totalPages,onPage){
    container.innerHTML='';if(!totalPages||totalPages<=1)return;
    const prev=document.createElement('button');prev.type='button';prev.textContent='Anterior';prev.disabled=page<=1;prev.addEventListener('click',()=>onPage(page-1));container.appendChild(prev);
    const start=Math.max(1,page-2),end=Math.min(totalPages,page+2);
    for(let i=start;i<=end;i++){const b=document.createElement('button');b.type='button';b.textContent=String(i);if(i===page)b.className='active';b.addEventListener('click',()=>onPage(i));container.appendChild(b);}
    const next=document.createElement('button');next.type='button';next.textContent='Siguiente';next.disabled=page>=totalPages;next.addEventListener('click',()=>onPage(page+1));container.appendChild(next);
  }
  function showMessage(el,text,type){if(!el)return;el.textContent=text||'';el.classList.remove('error','success');if(type)el.classList.add(type);}
  function skeletonCards(container,count){container.innerHTML='';for(let i=0;i<count;i++){const s=document.createElement('div');s.className='skeleton skeleton-card';container.appendChild(s);}}
  window.JeanPipiUI={safeImage,formatDate,articleCard,renderArticles,pagination,showMessage,skeletonCards};
})();
