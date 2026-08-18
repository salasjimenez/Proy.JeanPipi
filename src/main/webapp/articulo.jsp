<%-- Detalle publico de un articulo. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="app-context" content="${pageContext.request.contextPath}">
  <meta name="article-id" content="${article.id}">
  <title>${fn:escapeXml(empty article.seoTitulo ? article.titulo : article.seoTitulo)} | JEANPIPI</title>
  <meta name="description" content="${fn:escapeXml(empty article.seoDescripcion ? article.extracto : article.seoDescripcion)}">
  <link rel="canonical" href="${fn:escapeXml(article.seoCanonical)}">
  <meta property="og:type" content="article">
  <meta property="og:title" content="${fn:escapeXml(article.titulo)}">
  <meta property="og:description" content="${fn:escapeXml(article.extracto)}">
  <meta property="og:image" content="${fn:escapeXml(empty article.seoImagen ? article.portadaUrl : article.seoImagen)}">
  <meta property="og:url" content="${fn:escapeXml(article.seoCanonical)}">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<main class="article-page">
  <article class="container-xl article-layout">
    <header class="article-header">
      <c:if test="${not empty article.categoriaSlug}"><a class="eyebrow" href="${pageContext.request.contextPath}/categoria/${fn:escapeXml(article.categoriaSlug)}">${fn:escapeXml(article.categoriaNombre)}</a></c:if>
      <h1>${fn:escapeXml(article.titulo)}</h1>
      <p class="article-deck">${fn:escapeXml(article.extracto)}</p>
      <div class="article-meta"><span>Por ${fn:escapeXml(article.autorNombre)}</span><span>${article.minutosLectura} min de lectura</span><span><i class="bi bi-eye"></i> ${article.vistas}</span></div>
    </header>
    <c:if test="${not empty article.portadaUrl}"><figure class="article-cover"><img src="${fn:escapeXml(article.portadaUrl)}" alt="${fn:escapeXml(article.titulo)}"></figure></c:if>
    <div class="article-content"><c:out value="${article.contenido}" escapeXml="false"/></div>
    <div class="article-actions">
      <button id="favoriteButton" class="outline-button" type="button"><i class="bi bi-bookmark"></i> Guardar</button>
      <button class="outline-button" type="button" data-share="native"><i class="bi bi-share"></i> Compartir</button>
    </div>
  </article>
  <section class="container-xl section-block" aria-labelledby="relatedTitle"><div class="section-heading"><h2 id="relatedTitle">Tambien te puede interesar</h2><span class="section-rule"></span></div><div id="relatedGrid" class="article-grid four"></div></section>
  <section class="container-xl comments-section" aria-labelledby="commentsTitle">
    <div class="section-heading"><h2 id="commentsTitle">Comentarios</h2><span class="section-rule"></span></div>
    <form id="commentForm" class="comment-form">
      <div class="guest-fields" id="guestCommentFields"><input id="commentName" placeholder="Nombre" maxlength="120"><input id="commentEmail" type="email" placeholder="Correo opcional" maxlength="190"></div>
      <textarea id="commentContent" required minlength="2" maxlength="2000" placeholder="Escribe un comentario"></textarea>
      <button class="editorial-button" type="submit">Enviar a moderacion</button>
      <p id="commentMessage" class="form-message" role="status"></p>
    </form>
    <div id="commentsList" class="comments-list"></div>
  </section>
</main>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<script src="${pageContext.request.contextPath}/js/article.js"></script>
</body>
</html>
