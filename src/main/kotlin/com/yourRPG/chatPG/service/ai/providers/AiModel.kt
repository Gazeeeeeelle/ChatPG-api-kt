package com.yourRPG.chatPG.service.ai.providers

enum class AiModel {

    NONE(AiProvider.NONE, "none", "none"),

    GEMINI_2_5_FLASH(AiProvider.GOOGLE, "gemini-2.5-flash", "gemini-2.5-flash"),

    GPT_OSS_20B(AiProvider.CHUTES, "openai/gpt-oss-20b", "gpt-oss-20b"),

    TONGYI_DEEP_RESEARCH_30B_A3B(AiProvider.CHUTES, "Alibaba-NLP/Tongyi-DeepResearch-30B-A3B", "tongyi-deep-search"),

    GEMMA_3_4B_IT(AiProvider.CHUTES, "unsloth/gemma-3-4b-it", "gemma-3");

    private var provider: AiProvider

    private var modelName: String

    private var nickName: String

    constructor(provider: AiProvider, modelName: String, nickName: String) {
        this.provider = provider
        this.modelName = modelName
        this.nickName = nickName
    }

    fun getModelName(): String {
        return this.modelName
    }

    fun getNickName(): String {
        return this.nickName
    }

    fun getProvider(): AiProvider {
        return this.provider
    }

    companion object {
        fun findByModelName(modelName: String): AiModel? {
            AiModel.valueOf(modelName)

            return try {
                AiModel.valueOf(modelName)
            } catch (ex: IllegalArgumentException) {
                null
            }
        }



        fun findByNickName(nickName: String): AiModel? {
            return AiModel.entries.find { it.nickName == nickName }
        }
    }

}