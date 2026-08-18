<%-- Portada publica de una categoria. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="app-context" content="${pageContext.request.contextPath}">
  <meta name="category-slug" content="${fn:escapeXml(category.slug)}">
  <meta name="description" content="${fn:escapeXml(category.descripcion)}">
  <title>${fn:escapeXml(category.nombre)} | JEANPIPI</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<main class="container-xl">
  <section class="category-hero">
    <div><p class="eyebrow">Categoria</p><h1>${fn:escapeXml(category.nombre)}</h1><p>${fn:escapeXml(category.descripcion)}</p></div>
    <c:if test="${not empty category.portadaUrl}"><img src="${fn:escapeXml(category.portadaUrl)}" alt="Portada ${fn:escapeXml(category.nombre)}"></c:if>
  </section>
  <section class="section-block">
    <form id="categoryFilters" class="filter-bar">
      <input id="categoryQuery" type="search" placeholder="Buscar en ${fn:escapeXml(category.nombre)}" aria-label="Buscar en categoria">
      <select id="categoryOrder" aria-label="Ordenar"><option value="latest">Mas recientes</option><option value="trend">Tendencia</option><option value="views">Mas vistos</option><option value="favorites">Mas guardados</option></select>
      <button class="editorial-button small" type="submit">Aplicar</button>
    </form>
    <div id="categoryArticles" class="article-grid four"></div>
    <div id="categoryEmpty" class="empty-state d-none"><i class="bi bi-journal-x"></i><h2>Sin publicaciones</h2><p>No hay articulos disponibles con estos filtros.</p></div>
    <nav id="categoryPagination" class="pagination-wrap" aria-label="Paginacion"></nav>
  </section>
</main>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<script src="${pageContext.request.contextPath}/js/category.js"></script>
</body>
</html>
