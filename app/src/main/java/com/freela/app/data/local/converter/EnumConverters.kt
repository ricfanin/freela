package com.freela.app.data.local.converter

import androidx.room.TypeConverter
import com.freela.app.domain.model.FasePipeline
import com.freela.app.domain.model.Priorita
import com.freela.app.domain.model.StatoFattura
import com.freela.app.domain.model.StatoPreventivo
import com.freela.app.domain.model.StatoProgetto
import com.freela.app.domain.model.TipoInterazione

// converter enum->string uno per uno: più verboso ma esplicito e regge bene i rename degli enum
class EnumConverters {

    @TypeConverter fun fromFase(v: FasePipeline): String = v.name
    @TypeConverter fun toFase(s: String): FasePipeline = FasePipeline.valueOf(s)

    @TypeConverter fun fromTipoInterazione(v: TipoInterazione): String = v.name
    @TypeConverter fun toTipoInterazione(s: String): TipoInterazione = TipoInterazione.valueOf(s)

    @TypeConverter fun fromPriorita(v: Priorita): String = v.name
    @TypeConverter fun toPriorita(s: String): Priorita = Priorita.valueOf(s)

    @TypeConverter fun fromStatoPreventivo(v: StatoPreventivo): String = v.name
    @TypeConverter fun toStatoPreventivo(s: String): StatoPreventivo = StatoPreventivo.valueOf(s)

    @TypeConverter fun fromStatoFattura(v: StatoFattura): String = v.name
    @TypeConverter fun toStatoFattura(s: String): StatoFattura = StatoFattura.valueOf(s)

    @TypeConverter fun fromStatoProgetto(v: StatoProgetto): String = v.name
    @TypeConverter fun toStatoProgetto(s: String): StatoProgetto = StatoProgetto.valueOf(s)
}
