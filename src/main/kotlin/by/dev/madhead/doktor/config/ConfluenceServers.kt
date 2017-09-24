package by.dev.madhead.doktor.config

import by.dev.madhead.doktor.Messages
import by.dev.madhead.doktor.model.ResolvedConfluenceServer
import com.cloudbees.plugins.credentials.CredentialsMatchers
import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials
import hudson.AbortException
import hudson.Extension
import hudson.security.ACL
import jenkins.model.GlobalConfiguration
import jenkins.model.Jenkins
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import org.kohsuke.stapler.StaplerRequest

@Extension(ordinal = -100.0)
class ConfluenceServers : GlobalConfiguration() {
	var servers: List<ConfluenceServer> = emptyList()
		set(value) {
			field = value
			save()
		}

	init {
		load()
	}

	override fun configure(req: StaplerRequest, json: JSONObject?): Boolean {
		validate(json)
		req.bindJSON(this, json)

		return true
	}

	private fun validate(json: JSONObject?) {
		if ((null == json) || (null == json["servers"]) || (json["servers"] is JSONObject)) {
			return
		}
		if ((json["servers"] as JSONArray).groupBy { (it as JSONObject)["name"] }.any { it.value.size > 1 }) {
			throw FormException(Messages.doktor_config_ConfluenceServers_validation_name_dublicate(), "")
		}
	}

	companion object {
		fun resolve(server: String): ResolvedConfluenceServer {
			val confluenceServer = GlobalConfiguration.all().get(ConfluenceServers::class.java)?.servers?.find { it.name == server }
				?: throw AbortException(Messages.doktor_hudson_AbortException_unknownConfluenceServer())

			val credentials = if (!confluenceServer.credentials.isNullOrBlank()) {
				CredentialsMatchers.firstOrNull(
					CredentialsProvider.lookupCredentials(
						StandardUsernamePasswordCredentials::class.java,
						Jenkins.getInstance(),
						ACL.SYSTEM,
						emptyList()
					),
					CredentialsMatchers.withId(confluenceServer.credentials!!)
				) ?: throw AbortException(Messages.doktor_hudson_AbortException_unknownCredentials())
			} else {
				null
			}

			return ResolvedConfluenceServer(
				server,
				confluenceServer.url,
				confluenceServer.space,
				credentials?.username,
				credentials?.password?.plainText
			)
		}
	}
}
