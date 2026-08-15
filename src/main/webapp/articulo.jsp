<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JeanPipi | Lectura</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>
    <div class="container my-5" style="max-width: 800px;">
        <span id="categoria-articulo" class="text-uppercase text-muted fw-bold"></span>
        <h1 id="titulo-articulo" class="display-4 font-serif fw-bold my-3"></h1>
        <img id="imagen-articulo" class="img-fluid my-4 w-100" alt="Imagen del Artículo">
        <div id="contenido-articulo" class="fs-5 lh-lg mb-5"></div>
        <button class="btn btn-outline-dark" data-action="favorito">♥ Guardar en Favoritos</button>
        <a href="index.jsp" class="btn btn-link text-dark ms-3">Volver al inicio</a>
    </div>
    <script src="js/api.js"></script>
    <script src="js/articulos.js"></script>
    <script src="js/favoritos.js"></script>
</body>
</html>
