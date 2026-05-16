package com.freela.app.domain.model

/** Persone demo del design handoff per il seed iniziale + switcher in Settings. */
enum class PersonaDemo(val key: String, val displayName: String, val ruolo: String) {
    GIULIA("giulia", "Giulia", "Social Media Manager"),
    LUCA("luca", "Luca", "Web Designer"),
    SARA("sara", "Sara", "Fotografa");

    companion object {
        fun fromKey(key: String?): PersonaDemo? = entries.firstOrNull { it.key == key }
    }
}
