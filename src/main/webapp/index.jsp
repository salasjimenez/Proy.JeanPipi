<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JEANPIPI | Digital Fashion Magazine</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>

    <!-- Barra de Unificación Superior (Buscador, Sesión y Panel Admin) -->
    <nav class="navbar navbar-expand-lg navbar-light bg-light border-bottom py-2">
        <div class="container">
            <div class="d-flex align-items-center">
                <input type="text" id="input-busqueda" class="form-control form-control-sm me-2" placeholder="Buscar tendencia...">
                <button class="btn btn-sm btn-outline-dark" onclick="ejecutarBusqueda()"><i class="bi bi-search"></i> Buscar</button>
            </div>
            <div class="d-flex align-items-center gap-3">
                <a href="login.jsp" class="text-dark text-decoration-none small fw-bold"><i class="bi bi-person"></i> INICIAR SESIÓN</a>
                <a href="admin.jsp" class="btn btn-sm btn-dark text-uppercase px-3 small">PANEL ADMIN</a>
            </div>
        </div>
    </nav>

    <!-- Header con el Logo de Imagen oficial -->
    <header class="py-4 bg-white text-center">
        <div class="container">
            <a href="index.jsp" class="d-inline-block">
                <img src="img/JeanPipi.png" alt="JEANPIPI Logo" class="img-fluid" style="max-height: 85px;">
            </a>
        </div>
    </header>

    <!-- Navegación por Categorías -->
    <nav class="sub-nav bg-white mb-4">
        <div class="container d-flex justify-content-center flex-wrap">
            <a href="#" onclick="filtrarCategoria('Moda')">Moda</a>
            <a href="#" onclick="filtrarCategoria('Tendencias')">Tendencias</a>
            <a href="#" onclick="filtrarCategoria('Belleza')">Belleza</a>
            <a href="#" onclick="filtrarCategoria('Street Style')">Street Style</a>
            <a href="#" onclick="filtrarCategoria('Diseñadores')">Diseñadores</a>
            <a href="#" onclick="filtrarCategoria('Modelos')">Modelos</a>
            <a href="#" onclick="filtrarCategoria('Celebridades')">Celebridades</a>
            <a href="#" onclick="filtrarCategoria('Pasarelas')">Pasarelas</a>
        </div>
    </nav>

    <div class="container">
        <!-- Portada Principal Enlazada -->
        <section class="hero-card my-5">
            <div class="row align-items-center">
                <div class="col-lg-7">
                    <img id="hero-img" src="img/JeanPipi.png" class="img-fluid w-100 shadow-sm" style="max-height: 420px; object-fit: cover;" alt="Portada Principal">
                </div>
                <div class="col-lg-5 p-4">
                    <span class="text-uppercase text-muted fw-bold small">Editorial Destacado</span>
                    <h2 id="hero-title" class="hero-title mt-2 mb-3">Conectando...</h2>
                    <p id="hero-desc" class="text-secondary">Cargando la información más reciente desde la base de datos...</p>
                    <a id="hero-btn" href="#" class="btn btn-editorial mt-3">Leer Más</a>
                </div>
            </div>
        </section>

        <hr class="my-5">

        <!-- Tendencias de la Semana -->
        <section class="mb-5">
            <h3 class="font-serif fw-bold text-uppercase mb-4">Tendencias de la Semana</h3>
            <div class="row" id="contenedor-destacados"></div>
        </section>

        <!-- Últimos Artículos -->
        <section class="mb-5">
            <h3 class="font-serif fw-bold text-uppercase mb-4">Últimos Artículos</h3>
            <div class="row g-4" id="contenedor-articulos"></div>
        </section>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="js/app.js"></script>
</body>
</html>