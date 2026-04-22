document.addEventListener("DOMContentLoaded", () => {
  // Evitamos que el formulario recargue la página al enviarlo
  const formularioAcceso = document.getElementById("formulario-acceso");
  if (formularioAcceso) {
    formularioAcceso.addEventListener("submit", (evento) => {
      evento.preventDefault();
      window.location.href = "main.html"; 
    });
  }

  const contenedorTarjetas = document.getElementById("contenedor-tarjetas");
  if (contenedorTarjetas) {
    cargarXML(); 
  }
});

let tarjetasData = [];

function cargarXML() {
  fetch("data/data.xml")
    .then((respuesta) => {
      if (!respuesta.ok) throw new Error("No se ha encontrado el XML");
      return respuesta.text();
    })
    .then((cadenaXML) => {
      // Convertimos el texto bruto en un objeto XML navegable
      const parser = new DOMParser();
      const xmlDoc = parser.parseFromString(cadenaXML, "text/xml");
      const artistasXML = xmlDoc.getElementsByTagName("artista");

      for (let i = 0; i < artistasXML.length; i++) {
        let nodo = artistasXML[i];
        
        tarjetasData.push({
          titulo: nodo.getElementsByTagName("titulo")[0].textContent,
          descripcion: nodo.getElementsByTagName("descripcion")[0].textContent,
          img: nodo.getElementsByTagName("img")[0].textContent,
          audio: nodo.getElementsByTagName("audio")[0].textContent
        });
      }
      mostrarTarjetas(tarjetasData);
    })
    .catch((error) => console.error("Fallo al cargar el XML:", error));
}

const contenedor = document.getElementById("contenedor-tarjetas");
const modalTarjeta = document.getElementById("ventana-modal-tarjeta");
const modalTema = document.getElementById("ventana-modal-tema");
const modalInfo = document.getElementById("ventana-modal-info");

function mostrarTarjetas(data) {
  if (!contenedor) return; 
  contenedor.innerHTML = ""; // Limpiamos el contenedor antes de pintar (útil al usar el buscador)

  // Comprobamos el fondo actual para saber si las tarjetas necesitan el estilo oscuro o claro
  const bgColor = document.body.style.backgroundColor;
  const esModoClaro = bgColor === "rgb(224, 224, 224)" || bgColor === "#e0e0e0";

  data.forEach((artista) => {
    const div = document.createElement("div");
    div.classList.add("tarjeta-item"); 
    
    if (esModoClaro) {
        div.classList.add("tarjeta-modo-claro");
    }
    
    div.innerHTML = `
      <img src="${artista.img}" alt="${artista.titulo}" style="width: 100%; height: 120px; object-fit: cover; border-radius: 4px;">
      <h3>${artista.titulo}</h3>
      <p style="font-size: 0.9em; color: #ccc;">Haz clic para ver información</p>
    `;

    div.addEventListener("click", () => {
        abrirModalInfo(artista);
    });

    contenedor.appendChild(div);
  });
}

function abrirModalInfo(artista) {
    document.getElementById("info-titulo").textContent = artista.titulo;
    document.getElementById("info-imagen").src = artista.img;
    document.getElementById("info-descripcion").textContent = artista.descripcion;
    document.getElementById("info-audio").src = artista.audio;
    modalInfo.style.display = "flex";
}

const btnCerrarInfo = document.getElementById("btn-cerrar-info");
if (btnCerrarInfo) {
    btnCerrarInfo.addEventListener("click", () => {
        modalInfo.style.display = "none"; 
        // Reiniciamos el audio para que no siga sonando en segundo plano al cerrar
        const audio = document.getElementById("info-audio");
        audio.pause();
        audio.currentTime = 0; 
    });
}

const btnBuscar = document.getElementById("btn-buscar");
if (btnBuscar) {
    btnBuscar.addEventListener("click", () => {
      const filtro = document.getElementById("input-buscar").value.toLowerCase();
      const filtradas = tarjetasData.filter((t) =>
        t.titulo.toLowerCase().includes(filtro)
      );
      mostrarTarjetas(filtradas); 
    });
}

/* --- GESTIÓN DEL TEMA Y COLORES --- */
const btnOscuro = document.getElementById("btn-tema-oscuro");
const btnClaro = document.getElementById("btn-tema-claro");
const btnPersonalizado = document.getElementById("btn-tema-personalizado");

// Aplica o quita la clase de modo claro a las tarjetas ya renderizadas
function actualizarEstiloTarjetas(esClaro) {
    document.querySelectorAll(".tarjeta-item").forEach(t => {
        if (esClaro) t.classList.add("tarjeta-modo-claro");
        else t.classList.remove("tarjeta-modo-claro");
    });
}

if (btnOscuro) {
    btnOscuro.addEventListener("click", () => {
        document.body.style.backgroundColor = "#0a0a0a"; 
        document.body.style.color = "#f5f5f5"; 
        actualizarEstiloTarjetas(false);
    });
}

if (btnClaro) {
    btnClaro.addEventListener("click", () => {
        document.body.style.backgroundColor = "#e0e0e0"; 
        document.body.style.color = "#111"; 
        actualizarEstiloTarjetas(true);
    });
}

if (btnPersonalizado) {
    btnPersonalizado.addEventListener("click", () => {
        modalTema.style.display = "flex"; 
    });
}

const btnAceptarTema = document.getElementById("btn-aceptar-tema");
if (btnAceptarTema) {
    btnAceptarTema.addEventListener("click", () => {
      const colorElegido = document.getElementById("input-color-fondo").value;
      document.body.style.backgroundColor = colorElegido;
      modalTema.style.display = "none";

      // Adaptamos el color del texto si el fondo elegido es muy claro
      const brillo = calcularBrilloHex(colorElegido);
      const esColorClaro = brillo > 186; 

      document.body.style.color = esColorClaro ? "#111" : "#f5f5f5";
      actualizarEstiloTarjetas(esColorClaro);
    });
}

// Fórmula estándar para calcular la luminancia de un color Hexadecimal
function calcularBrilloHex(hex) {
    hex = hex.replace('#', '');
    const r = parseInt(hex.substring(0, 2), 16);
    const g = parseInt(hex.substring(2, 4), 16);
    const b = parseInt(hex.substring(4, 6), 16);
    return (r * 0.299 + g * 0.587 + b * 0.114);
}

const btnVolver = document.getElementById("btn-volver");
if (btnVolver) {
    btnVolver.addEventListener("click", () => {
        window.location.href = "index.html";
    });
}

/* --- CREACIÓN DE NUEVAS TARJETAS --- */
function abrirModalAñadir() {
  modalTarjeta.style.display = "flex";
}

const btnAñadirCabecera = document.getElementById("btn-añadir-cabecera");
if (btnAñadirCabecera) btnAñadirCabecera.addEventListener("click", abrirModalAñadir);

const btnCerrarAñadir = document.getElementById("btn-cerrar-modal-tarjeta");
if (btnCerrarAñadir) {
    btnCerrarAñadir.addEventListener("click", () => {
      modalTarjeta.style.display = "none";
    });
}

const btnGuardarTarjeta = document.getElementById("btn-guardar-tarjeta");
if (btnGuardarTarjeta) {
    btnGuardarTarjeta.addEventListener("click", () => {
      const titulo = document.getElementById("input-titulo-tarjeta").value;
      const descripcion = document.getElementById("input-descripcion-tarjeta").value;
      const imgFile = document.getElementById("input-imagen-tarjeta").files[0]; 

      if (titulo && descripcion && imgFile) {
        // Usamos FileReader para convertir el archivo de imagen en una URL
        const reader = new FileReader(); 
        reader.onload = function (e) {
          tarjetasData.push({
            titulo: titulo,
            descripcion: descripcion,
            img: e.target.result,
            audio: "" 
          });
          mostrarTarjetas(tarjetasData);
          modalTarjeta.style.display = "none";
          
          document.getElementById("input-titulo-tarjeta").value = "";
          document.getElementById("input-descripcion-tarjeta").value = "";
          document.getElementById("input-imagen-tarjeta").value = "";
        };
        reader.readAsDataURL(imgFile); 
      }
    });
}