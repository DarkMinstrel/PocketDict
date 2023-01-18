package com.darkminstrel.pocketdict.models

data class TranslationPair(
    val first: String,
    val second: String,
)

data class ParsedTranslation(
    val source: String,
    val langFrom: String,
    val langTo: String,
    val transcription: String?,
    val defaultContexts: List<TranslationPair>,
    private val items: Map<String, List<TranslationPair>>,
) {
    val sortedItems by lazy {
        items.toList().sortedBy { (_, contexts) -> if (contexts.isEmpty()) 1 else 0 }.toMap()
    }

    fun getPairs(context: String?): List<TranslationPair> = when (context) {
        null -> defaultContexts
        else -> sortedItems[context]
    } ?: emptyList()

    val defaultContext: String? by lazy {
        when {
            defaultContexts.isEmpty() -> sortedItems.toList().firstOrNull()?.first
            else -> null
        }
    }

    val description: String by lazy {
        this.sortedItems.toList().joinToString(", ") { it.first }
    }

    fun mergeWith(other: ParsedTranslation): ParsedTranslation {
        val source = this.source
        val langFrom = this.langFrom
        val langTo = this.langTo
        val transcription = this.transcription ?: other.transcription
        val defaultContexts = this.defaultContexts + other.defaultContexts
        val items = (this.items.keys + other.items.keys).associateWith {
            (items[it] ?: emptyList()) + (other.items[it] ?: emptyList())
        }
        return ParsedTranslation(source, langFrom, langTo, transcription, defaultContexts, items)
    }

}

val demo: ParsedTranslation = ParsedTranslation(
    source = "hello",
    langFrom = "en",
    langTo = "ru",
    transcription = "həˈloʊ",
    defaultContexts = listOf(
        TranslationPair("And now for a proper <b>hello</b>.", "Ну а теперь, в качестве нормального <b>приветствия</b>.")
    ),
    items = mapOf(
        "привет" to listOf(
            TranslationPair("Hence 07734 became \"<b>hello</b>\".", "Следовательно 07734 стал hello - \"<b>привет</b>\"."),
            TranslationPair("So long physical buttons and <b>hello</b> on-screen controls.", "Так что длинные физические кнопки и <b>привет</b> на экране управления.")
        ),
        "эй" to emptyList(),
        "приветствие" to listOf(
            TranslationPair("It can mean both <b>hello</b> and goodbye.", "Означать оно может одновременно как <b>приветствие</b>, так и прощание.")
        )
    )

)