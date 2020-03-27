package com.darkminstrel.pocketdict.api

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL = "https://cps.reverso.net/"

class ApiImpl {
    fun makeRetrofitService(): ApiInterface {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(ApiInterface::class.java)
    }

}


/*
{
  "error": false,
  "success": true,
  "sources": [
    {
      "weight": 0,
      "count": 9685,
      "source": "hello",
      "displaySource": "hello",
      "translations": [
        {
          "translation": "привет",
          "count": 4035,
          "contexts": [
            {
              "source": "Why, <em>hello</em>, there, Admiral.",
              "target": "А, Адмирал, <em>привет</em>, что здесь делаешь.",
              "favoriteId": null,
              "isGood": true
            },
            {
              "source": "Tell him Norma Bates said <em>hello</em>.",
              "target": "Скажите ему, что Норма Бейтс передаёт <em>привет</em>.",
              "favoriteId": null,
              "isGood": true
            },
            {
              "source": "Tell her slipping' Jimmy says <em>hello</em>.",
              "target": "Передай ей, что Скользкий Джимми передает ей <em>привет</em>.",
              "favoriteId": null,
              "isGood": true
            },
            {
              "source": "Never said <em>hello</em>, timelines and all that.",
              "target": "Никогда не говорил, <em>привет</em>, о времени и всем-таком.",
              "favoriteId": null,
              "isGood": true
            },
            {
              "source": "Tell Martha... I said <em>hello</em>.",
              "target": "Скажи Марте... что я передаю ей <em>привет</em>.",
              "favoriteId": null,
              "isGood": true
            },
            {
              "source": "Tell my two favorite ex-girlfriends I said <em>hello</em>.",
              "target": "Передавай моим двум бывшим подружкам <em>привет</em> от меня.",
              "favoriteId": null,
              "isGood": true
            },
            {
              "source": "Justmeforyou and you for me... <em>hello</em>...",
              "target": "Просто я для тебя, а ты для меня... <em>Привет</em>...",
              "favoriteId": null,
              "isGood": true
            },
            {
              "source": "She'll take my head off h... <em>hello</em>, Selena.",
              "target": "Она мне голову оторвет, если... <em>Привет</em>, Селена.",
              "favoriteId": null,
              "isGood": true
            },
            {
              "source": "Well, <em>hello</em>, Burt Munro from New Zealand down under.",
              "target": "<em>Привет</em>, Берт Манро из Новой Зеландии, страны антиподов.",
              "favoriteId": null,
              "isGood": true
            },
            {
              "source": "Tell him his daughter says <em>hello</em>.",
              "target": "Передавайте ему <em>привет</em> от его дочери.",
              "favoriteId": null,
              "isGood": true
            },
            {
              "source": "Yes, <em>hello</em>, doughnut king.",
              "target": "Да, <em>привет</em>, король пончиков.",
              "favoriteId": null,
              "isGood": true
            },
            {
              "source": "Well, <em>hello</em> to you, too.",
              "target": "Ну и вам тоже, <em>привет</em>.",
              "favoriteId": null,
              "isGood": true
            },
            {
              "source": "And I, <em>hello</em>, am the director.",
              "target": "И я - <em>привет</em> - и есть режиссёр.",
              "favoriteId": null,
              "isGood": true
            },
            {
              "source": "You always make my day when you wave and smile <em>hello</em>.",
              "target": "Ты всегда радуешь меня, когда машешь и улыбаешься, говоря <em>привет</em>.",
              "favoriteId": null,
              "isGood": true
            },
            {
              "source": "And <em>hello</em> to you, too, my firstborn.",
              "target": "И тебе <em>привет</em>, мой первенец.",
              "favoriteId": null,
              "isGood": true
            }
          ],
          "isFromDict": true,
          "pos": "nm",
          "isRude": false,
          "isSlang": false,
          "isReverseValidated": false,
          "isGrayed": false,
          "favoriteId": null
        },
        {
          "translation": "алло",
          "count": 1083,
          "contexts": [],
          "isFromDict": false,
          "pos": "adv",
          "isRude": false,
          "isSlang": true,
          "isReverseValidated": true,
          "isGrayed": false,
          "favoriteId": null
        },
        {
          "translation": "эй",
          "count": 256,
          "contexts": [],
          "isFromDict": false,
          "pos": "n",
          "isRude": false,
          "isSlang": false,
          "isReverseValidated": true,
          "isGrayed": false,
          "favoriteId": null
        },
        {
          "translation": "приветствую",
          "count": 79,
          "contexts": [],
          "isFromDict": false,
          "pos": "adv",
          "isRude": false,
          "isSlang": false,
          "isReverseValidated": true,
          "isGrayed": false,
          "favoriteId": null
        },
        {
          "translation": "приветствие",
          "count": 38,
          "contexts": [],
          "isFromDict": false,
          "pos": "nn",
          "isRude": false,
          "isSlang": false,
          "isReverseValidated": true,
          "isGrayed": false,
          "favoriteId": null
        },
        {
          "translation": "приветик",
          "count": 36,
          "contexts": [],
          "isFromDict": false,
          "pos": "n",
          "isRude": false,
          "isSlang": false,
          "isReverseValidated": true,
          "isGrayed": false,
          "favoriteId": null
        },
        {
          "translation": "Добрый вечер",
          "count": 32,
          "contexts": [],
          "isFromDict": false,
          "pos": "adv",
          "isRude": false,
          "isSlang": false,
          "isReverseValidated": true,
          "isGrayed": false,
          "favoriteId": null
        },
        {
          "translation": "хелло",
          "count": 22,
          "contexts": [],
          "isFromDict": true,
          "pos": "adv",
          "isRude": false,
          "isSlang": false,
          "isReverseValidated": false,
          "isGrayed": false,
          "favoriteId": null
        },
        {
          "translation": "здравствуйте",
          "count": 3033,
          "contexts": [
            {
              "source": "Well, <em>hello</em>, Mrs. Worley.",
              "target": "Ну, что ж, <em>здравствуйте</em>, миссис Уорли.",
              "favoriteId": null,
              "isGood": true
            },
            {
              "source": "So anyway, <em>hello</em> and goodbye.",
              "target": "Ну, так что... <em>здравствуйте</em> и прощайте.",
              "favoriteId": null,
              "isGood": true
            },
            {
              "source": "Well, <em>hello</em>, Mrs Flusky.",
              "target": "Ну что ж, <em>здравствуйте</em>, мистер Флацки.",
              "favoriteId": null,
              "isGood": true
            },
            {
              "source": "I mean, Dragonfly linn, <em>hello</em>.",
              "target": "То есть, Гостиница \"Стрекоза\", <em>здравствуйте</em>.",
              "favoriteId": null,
              "isGood": true
            },
            {
              "source": "Mr. Agos... first of all, <em>hello</em>.",
              "target": "Мистер Агос, прежде всего, <em>здравствуйте</em>.",
              "favoriteId": null,
              "isGood": true
            }
          ],
          "isFromDict": true,
          "pos": "",
          "isRude": false,
          "isSlang": false,
          "isReverseValidated": false,
          "isGrayed": false,
          "favoriteId": null
        },
        {
          "translation": "здравствуй",
          "count": 572,
          "contexts": [],
          "isFromDict": true,
          "pos": "",
          "isRude": false,
          "isSlang": false,
          "isReverseValidated": false,
          "isGrayed": false,
          "favoriteId": null
        },
        {
          "translation": "добрый день",
          "count": 161,
          "contexts": [],
          "isFromDict": false,
          "pos": "",
          "isRude": false,
          "isSlang": false,
          "isReverseValidated": true,
          "isGrayed": false,
          "favoriteId": null
        },
        {
          "translation": "здрасьте",
          "count": 84,
          "contexts": [],
          "isFromDict": false,
          "pos": "",
          "isRude": false,
          "isSlang": false,
          "isReverseValidated": true,
          "isGrayed": false,
          "favoriteId": null
        },
        {
          "translation": "здрасте",
          "count": 41,
          "contexts": [],
          "isFromDict": false,
          "pos": "",
          "isRude": false,
          "isSlang": false,
          "isReverseValidated": true,
          "isGrayed": false,
          "favoriteId": null
        },
        {
          "translation": "алё",
          "count": 38,
          "contexts": [],
          "isFromDict": true,
          "pos": "",
          "isRude": false,
          "isSlang": false,
          "isReverseValidated": false,
          "isGrayed": false,
          "favoriteId": null
        },
        {
          "translation": "Hello",
          "count": 167,
          "contexts": [],
          "isFromDict": false,
          "pos": "",
          "isRude": false,
          "isSlang": false,
          "isReverseValidated": true,
          "isGrayed": false,
          "favoriteId": null
        }
      ],
      "spellCorrected": false,
      "isSourceSplitted": false,
      "isDirectionChanged": false,
      "directionFrom": "en",
      "directionTo": "ru",
      "dymApplied": false,
      "isBaseForm": false
    }
  ],
  "additional": [],
  "wordSentence": "hello",
  "wordPos": 0,
  "debug": [],
  "historyId": null
}
 */