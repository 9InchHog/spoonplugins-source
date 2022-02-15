/*
 * Copyright (c) 2019 Owain van Brakel <https:github.com/Owain94>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

rootProject.name = "Spoon Plugins"

//------------------------------------------------------------//
// Client Plugins
//------------------------------------------------------------//
    ///*
include(":alchemicalhydra")
include(":animationcooldown")
include(":aoe")
include(":ariatob")
include(":autohop")
include(":azscreenmarkers")
include(":bingo")
include(":blackjack")
include(":bobigsplits")
include(":bonylo")
include(":cclumphelper")
include(":corpboost")
include(":coxadditions")
include(":coxentrance")
include(":coxfloorsplits")
include(":coxoverloadtimers")
include(":coxprep")
include(":coxspoontimers")
include(":cursed")
include(":customclientresizing")
include(":deasyscape")
include(":decorhighlight")
include(":defaulttab")
include(":detailedtimers")
include(":detachedcamerahotkey")
include(":dnightmare")
include(":dxpdrops")
include(":effecttimers")
include(":entityhiderplus")
include(":godbook")
include(":grotesqueguardiansext")
include(":gwdessencehider")
include(":gwdtimers")
include(":hideprayers")
include(":hideunder")
include(":hoptimer")
include(":infernoretard")
include(":keydrag")
include(":killswitch")
include(":lowmemorybloat")
include(":mouseovertext")
include(":multiindicators")
include(":neverlog")
include(":objecthider")
include(":outgoingchatfilter")
include(":phoenixnecklace")
include(":pvpplayerindicators")
//include(":raidscouterext")
include(":raidspoints")
include(":rareimplings")
include(":ratjam")
include(":reflection")
include(":reorderprayers")
include(":runedoku")
include(":shiftclickwalker")
include(":socketspoon")
include(":soulwars")
include(":spawnpredictor")
include(":specbar")
include(":specinfobox")
include(":specorb")
include(":spellbook")
include(":spoonannoyancemute")
include(":spoonbarrows")
//include(":spoonboosts")
include(":spoondemonicgorilla")
include(":spoonezswaps")
include(":spoongauntlet")
//include(":spoongrounditems")
//include(":spoongroundmarkers")
//include(":spoonitemcharges")
include(":spoonjadhelper")
include(":spoonkeyremapping")
include(":spoonkilltimers")
include(":spoonnex")
include(":spoonnightmare")
//include(":spoonnpchighlight")
//include(":spoonobjectindicators")
//include(":spoonopponentinfo")
include(":spoonrunecraft")
include(":spoonscenereloader")
include(":spoonsepulchre")
//include(":spoonslayer")
include(":spoonsmithing")
include(":spoontempoross")
//include(":spoontileindicators")
include(":spoontob")
include(":spoontobstats")
include(":spoonvm")
include(":spoonvorkath")
include(":spoonzalcano")
//include(":theatre")
include(":tickdebug")
include(":tmorph")
include(":tobsounds")
include(":togglechat")
include(":toggleoverlays")
include(":tzhaartimers")
include(":vanguards")
include(":vengcounter")
include(":vmswimshamer")
include(":yurinex")
include(":yuritheatre")
include(":zulrah")
include(":zuktimer")
    //*/

//------------------------------------------------------------//
// Public Plugins
//------------------------------------------------------------//
    /*
include(":alchemicalhydra")
include(":aoe")
include(":ariatob")
include(":bingo")
include(":blackjack")
include(":bobigsplits")
include(":bonylo")
include(":corpboost")
include(":coxadditions")
include(":coxentrance")
include(":coxfloorsplits")
include(":coxoverloadtimers")
include(":coxprep")
include(":coxspoontimers")
include(":cursed")
include(":deasyscape")
include(":detachedcamerahotkey")
include(":dnightmare")
include(":dxpdrops")
include(":entityhiderplusspoon")
include(":grotesqueguardiansext")
include(":gwdessencehider")
include(":gwdtimers")
include(":hideprayers")
include(":hideunder")
include(":hoptimer")
include(":keydrag")
include(":mouseovertext")
include(":neverlog")
include(":outgoingchatfilter")
include(":phoenixnecklace")
include(":pvpplayerindicators")
//include(":raidscouterext")
include(":rareimplings")
include(":ratjam")
include(":shiftclickwalker")
include(":socket")
include(":soulwars")
include(":spoonannoyancemute")
include(":spoonbarrows")
include(":spoonboosts")
include(":spoondemonicgorilla")
include(":spoonezswaps")
include(":spoongauntlet")
include(":spoongrounditems")
include(":spoongroundmarkers")
include(":spoonitemcharges")
include(":spoonjadhelper")
include(":spoonkeyremapping")
include(":spoonkilltimers")
include(":spoonnex")
include(":spoonnightmare")
include(":spoonnpchighlight")
include(":spoonobjectindicators")
include(":spoonopponentinfo")
include(":spoonrunecraft")
include(":spoonscenereloader")
include(":spoonsepulchre")
//include(":spoonslayer")
include(":spoonsmithing")
include(":spoontempoross")
include(":spoontileindicators")
include(":spoontob")
include(":spoontobstats")
include(":spoonvm")
include(":spoonvorkath")
include(":spoonzalcano")
include(":tickdebug")
include(":tmorph")
include(":tobsounds")
include(":togglechat")
include(":tzhaartimers")
include(":vanguards")
include(":vmswimshamer")
include(":yurinex")
include(":yuritheatre")
    */

for (project in rootProject.children) {
    project.apply {
        projectDir = file(name)
        buildFileName = "$name.gradle.kts"

        require(projectDir.isDirectory) { "Project '${project.path} must have a $projectDir directory" }
        require(buildFile.isFile) { "Project '${project.path} must have a $buildFile build script" }
    }
}
