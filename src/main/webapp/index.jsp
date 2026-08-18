<%-- Portada publica de JeanPipi. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="JeanPipi, revista digital de moda, tendencias, belleza y cultura visual.">
  <meta name="app-context" content="${pageContext.request.contextPath}">
  <title>JEANPIPI | Digital Fashion Magazine</title>
  <link rel="preconnect" href="https://cdn.jsdelivr.net">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<main>
  <section id="heroSection" class="container-xl hero-section" aria-labelledby="heroTitle">
    <div id="heroSkeleton" class="skeleton hero-skeleton"></div>
    <article id="heroArticle" class="hero-editorial d-none">
      <div class="hero-media"><img id="heroImage" alt="" loading="eager"></div>
      <div class="hero-copy"><p class="eyebrow" id="heroCategory">Editorial</p><h1 id="heroTitle"></h1><p id="heroExcerpt"></p><a id="heroLink" class="editorial-button" href="#">Leer articulo</a></div>
    </article>
  </section>

  <section id="trendingSection" class="container-xl section-block" aria-labelledby="trendTitle">
    <div class="section-heading"><div><p class="eyebrow">Ahora</p><h2 id="trendTitle">Tendencias</h2></div><span class="section-rule"></span></div>
    <div id="trendingGrid" class="article-grid three"></div>
  </section>

  <section id="latestSection" class="container-xl section-block" aria-labelledby="latestTitle">
    <div class="section-heading"><div><p class="eyebrow">Edicion digital</p><h2 id="latestTitle">Ultimos articulos</h2></div><span class="section-rule"></span></div>
    <form id="articleFilters" class="filter-bar" aria-label="Filtros de articulos">
      <input type="search" id="filterQuery" placeholder="Buscar titulo, contenido o autor" aria-label="Texto de busqueda">
      <input type="text" id="filterAuthor" placeholder="Autor" aria-label="Autor">
      <select id="filterCategory" aria-label="Categoria"><option value="">Todas las categorias</option></select>
      <select id="filterOrder" aria-label="Ordenar"><option value="latest">Mas recientes</option><option value="trend">Tendencia</option><option value="views">Mas vistos</option><option value="favorites">Mas guardados</option></select>
      <button class="editorial-button small" type="submit">Aplicar</button>
    </form>
    <div id="articlesSkeleton" class="article-grid four"></div>
    <div id="articlesGrid" class="article-grid four"></div>
    <div id="articlesEmpty" class="empty-state d-none"><i class="bi bi-journal-x"></i><h3>No hay articulos</h3><p>Prueba con otros filtros de busqueda.</p></div>
    <nav id="pagination" class="pagination-wrap" aria-label="Paginacion"></nav>
  </section>
</main>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
