const SpeechSDK = window.SpeechSDK;
const AZURE_SPEECH_KEY = "sua-chave-aqui";
const AZURE_SPEECH_REGION = "eastus";

const textoDiv = document.getElementById('texto');
const respostaDiv = document.getElementById('resposta');
const btn = document.getElementById("button-voice");

let fluxoEmExecucao = false;

btn.addEventListener("click", () => {
    if (!fluxoEmExecucao) iniciarFluxo();
});

document.addEventListener("keydown", async function (e) {
    if (e.code === "Space") {
        e.preventDefault();
        if (!fluxoEmExecucao && !speechSynthesis.speaking) {
            iniciarFluxo();
        }
    }
});

async function iniciarFluxo() {
    fluxoEmExecucao = true;
    document.getElementById("button-icon").classList.remove("fa-volume-xmark");
    document.getElementById("button-icon").classList.add("fa-volume-high");

    try {
        await falarTexto("Faça sua consulta.");
        const texto = await ouvirTexto();
        if (!texto) {
            fluxoEmExecucao = false;
            return;
        }

        textoDiv.textContent = "Você disse: " + texto;
        const resposta = await buscarRespostaDaAPI(texto);

        if (resposta) {
            respostaDiv.textContent = "Resposta: " + resposta;
            await falarTexto(resposta);
            await perguntarSeDesejaContinuar();
        }
    } catch (e) {
        console.error("Erro no fluxo:", e);
        respostaDiv.textContent = "Erro durante o fluxo.";
    } finally {
        fluxoEmExecucao = false;
    }
}

function ouvirTexto() {
    return new Promise((resolve) => {
        const speechConfig = SpeechSDK.SpeechConfig.fromSubscription(AZURE_SPEECH_KEY, AZURE_SPEECH_REGION);
        speechConfig.speechRecognitionLanguage = "pt-BR";
        const audioConfig = SpeechSDK.AudioConfig.fromDefaultMicrophoneInput();
        const recognizer = new SpeechSDK.SpeechRecognizer(speechConfig, audioConfig);

        recognizer.recognizeOnceAsync(result => {
            if (result.reason === SpeechSDK.ResultReason.RecognizedSpeech) {
                resolve(result.text);
            } else {
                console.error("Erro ao reconhecer fala:", result.errorDetails);
                resolve(null);
            }
        });
    });
}

function falarTexto(texto) {
    return new Promise((resolve) => {
        const speechConfig = SpeechSDK.SpeechConfig.fromSubscription(AZURE_SPEECH_KEY, AZURE_SPEECH_REGION);
        speechConfig.speechSynthesisLanguage = "pt-BR";
        speechConfig.speechSynthesisVoiceName = "pt-BR-FranciscaNeural";
        const audioConfig = SpeechSDK.AudioConfig.fromDefaultSpeakerOutput();
        const synthesizer = new SpeechSDK.SpeechSynthesizer(speechConfig, audioConfig);

        synthesizer.speakTextAsync(texto,
            result => {
                if (result.reason === SpeechSDK.ResultReason.SynthesizingAudioCompleted) {
                    resolve();
                } else {
                    console.error("Erro ao falar:", result.errorDetails);
                    resolve();
                }
            },
            error => {
                console.error("Erro de síntese:", error);
                resolve();
            }
        );
    });
}

async function perguntarSeDesejaContinuar() {
    await falarTexto("Deseja perguntar mais alguma coisa?");
    const resposta = await ouvirTexto();
    if (!resposta) return;

    const respostaFormatada = resposta.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, '');

    if (respostaFormatada.includes("nao") || respostaFormatada.includes("não")) {
        await falarTexto("Ok, até a próxima!");
        respostaDiv.textContent = "Encerrado.";
        document.getElementById("button-icon").classList.remove("fa-volume-high");
        document.getElementById("button-icon").classList.add("fa-volume-xmark");
    } else {
        iniciarFluxo();
    }
}

async function buscarRespostaDaAPI(texto) {
    try {
        const response = await fetch("/consulta?texto=" + encodeURIComponent(texto));
        return await response.text();
    } catch (e) {
        console.error("Erro na API:", e);
        return "Erro ao consultar o assistente.";
    }
}
