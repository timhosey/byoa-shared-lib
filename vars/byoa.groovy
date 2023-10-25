import hudson.model.*
import jenkins.model.*
import hudson.slaves.*
import com.cloudbees.hudson.plugins.folder.*
import com.cloudbees.jenkins.plugins.foldersplus.*

def add(String nodeName) {
  String folderName = currentBuild.rawBuild.getParent().getFullName();
  print "Working on " + folderName
  // Slave node = Jenkins.instance.getNode(nodeName)
  // node.getNodeProperties().add(new SecurityTokensNodeProperty(false));
  // SecurityToken token = SecurityToken.newInstance();
  // node.getNodeProperties().get(SecurityTokensNodeProperty.class).addSecurityToken(token);
  // node.save()
  // SecurityGrant request = SecurityGrant.newInstance();
  // SecurityGrant grant = token.issue(request);

  // folder = Jenkins.instance.getItemByFullName(folderName)
  // if (jenkins.model.Jenkins.instance.getItemByFullName(folderName) == null) {
  //   folder = Jenkins.instance.createProject(Folder.class, folderName)
  // }

  // folder.getProperties().replace(new SecurityGrantsFolderProperty(Collections.<SecurityGrant>emptyList()));
  // folder.getProperties().get(SecurityGrantsFolderProperty.class).addSecurityGrant(grant);
  // folder.save()
  // print "Agent has been added successfully."
}