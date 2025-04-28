const textoDiv = document.getElementById('texto');
const respostaDiv = document.getElementById('resposta');
const btnFalar = document.getElementById('btnFalar');

// Função para falar uma mensagem
function falar(mensagem, callback) {
    const utterance = new SpeechSynthesisUtterance(mensagem);
    utterance.lang = 'pt-BR';
    utterance.onend = callback;
    speechSynthesis.speak(utterance);
}

// Função para escutar o usuário
function escutar(callback) {
    const recognition = new (window.SpeechRecognition || window.webkitSpeechRecognition)();
    recognition.lang = 'pt-BR';
    recognition.continuous = false;
    recognition.interimResults = false;

    recognition.onresult = (event) => {
        recognition.stop();
        const resultado = event.results[0][0].transcript.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
        console.log("Usuário disse:", resultado);
        callback(resultado);
    };

    recognition.start();
}

// Fluxo principal controlado
async function iniciarConsulta() {
    escutar(async (resultado) => {
        textoDiv.textContent = "Você disse: " + resultado;

        if (resultado.includes("nao")) {
            textoDiv.textContent = "Encerrando atendimento.";
            respostaDiv.textContent = "";
            falar("Ok, até a próxima!", () => {});
            return; // <- PARA TUDO AQUI MESMO, NÃO FAZ FETCH
        }

        try {
            const response = await fetch("/consulta?texto=" + encodeURIComponent(resultado));
            const respostaTexto = await response.text();
            respostaDiv.textContent = "Resposta: " + respostaTexto;

            falar(respostaTexto, () => {
                falar("Deseja mais alguma coisa?", () => {
                    iniciarConsulta(); // Continua se quiser
                });
            });

        } catch (error) {
            respostaDiv.textContent = "Erro ao consultar o assistente.";
            console.error(error);
        }
    });
}

// Ao pressionar Enter, inicia a consulta
document.addEventListener("keydown", (e) => {
    if (e.code === "Enter") {
        respostaDiv.textContent = "Faça sua consulta.";
        falar("Faça sua consulta.", () => {
            iniciarConsulta();
        });
    }
});

// Escutar consulta ao clicar no botão
btnFalar.addEventListener("click", () => {
    respostaDiv.textContent = "Faça sua consulta.";
    falar("Faça sua consulta.", () => {
        iniciarConsulta();
    });
});
