package by.dev.madhead.doktor.config

import by.dev.madhead.doktor.Messages
import com.cloudbees.plugins.credentials.CredentialsMatchers
import com.cloudbees.plugins.credentials.common.StandardListBoxModel
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials
import hudson.Extension
import hudson.model.AbstractDescribableImpl
import hudson.model.Descriptor
import hudson.security.ACL

import hudson.util.FormValidation
import jenkins.model.Jenkins
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.QueryParameter

class ConfluenceServer
@DataBoundConstructor
constructor(
	val name: String,
	val url: String,
	val space: String,
	val credentials: String?
) : AbstractDescribableImpl<ConfluenceServer>() {
	@Extension
	class ConfluenceServerDescriptor : Descriptor<ConfluenceServer>() {
		fun doFillCredentialsItems() = StandardListBoxModel()
			.includeEmptyValue()
			.includeMatchingAs(ACL.SYSTEM, Jenkins.getInstance(), StandardUsernamePasswordCredentials::class.java, emptyList(), CredentialsMatchers.always())

		fun doCheckName(@QueryParameter value: String?): FormValidation {
			if (value.isNullOrBlank()) {
				return FormValidation.error(Messages.doktor_config_ConfluenceServer_validation_name_empty())
			}

			return FormValidation.ok()
		}

		fun doCheckUrl(@QueryParameter value: String?): FormValidation {
			if (value.isNullOrBlank()) {
				return FormValidation.error(Messages.doktor_config_ConfluenceServer_validation_url_empty())
			}

			return FormValidation.ok()
		}

		fun doCheckSpace(@QueryParameter value: String?): FormValidation {
			if (value.isNullOrBlank()) {
				return FormValidation.error(Messages.doktor_config_ConfluenceServer_validation_space_empty())
			}

			return FormValidation.ok()
		}
	}
}
