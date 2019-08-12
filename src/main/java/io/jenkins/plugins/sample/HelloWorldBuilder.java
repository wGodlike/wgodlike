package io.jenkins.plugins.sample;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;

import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;

public class HelloWorldBuilder extends Builder implements SimpleBuildStep {

    private final String host;
    private final String service;
    private final String task;

    @DataBoundConstructor
    public HelloWorldBuilder(String host, String service, String task) {
        this.host = host;
        this.service = service;
        this.task = task;
    }

    public String getHost() {
        return host;
    }

    public String getService() {
        return service;
    }

    public String getTask() {
        return task;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        listener.getLogger().println("<--------------------------------------------------------------------------------------------------------------------------------->");
        listener.getLogger().println("                                               wGodlike Jenkins Plugin Version 1.0.00                                                ");
        listener.getLogger().println("                                                This plugin create by wGodlike                                                ");
        listener.getLogger().println("<--------------------------------------------------------------------------------------------------------------------------------->");
        // 根据host,service和task来拼接请求,此请求用于触发任务.
        String url = this.host + "/" + this.service + "/api/v1/trigger";
        url = url.trim();
        listener.getLogger().println("wGodlike platform -> now trigger task " + this.task + ", please wait for a moment...");
        String result = this.httpPost(url, listener);
    }

    public String httpPost(String url, TaskListener listener) {
        // <dependency>
        //            <groupId>org.apache.httpcomponents</groupId>
        //            <artifactId>httpclient</artifactId>
        //            <version>4.5.8</version>
        //        </dependency>
        //        <!-- https://mvnrepository.com/artifact/org.apache.httpcomponents/httpcore -->
        //        <dependency>
        //            <groupId>org.apache.httpcomponents</groupId>
        //            <artifactId>httpcore</artifactId>
        //            <version>4.4.11</version>
        //        </dependency>
        String result = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        EntityBuilder builder = EntityBuilder.create();
        try {
            List<BasicNameValuePair> list = new LinkedList<>();
            BasicNameValuePair parameter = new BasicNameValuePair("id", this.task);
            list.add(parameter);
            UrlEncodedFormEntity entityParam = new UrlEncodedFormEntity(list, "UTF-8");
            HttpPost request = new HttpPost(url);
            request.setEntity(entityParam);
            listener.getLogger().println("wGodlike platform -> " + request.getURI());
            CloseableHttpResponse response = httpclient.execute(request);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity);
//                    listener.getLogger().println("sosotest platform status: " + response.getStatusLine().toString());
//                    listener.getLogger().println("sosotest platform content: " + result);
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            listener.getLogger().println("ClientProtocolException");
            listener.getLogger().println(e.getMessage());
            e.printStackTrace();
        } catch (ParseException e) {
            listener.getLogger().println("ParseException");
            listener.getLogger().println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            listener.getLogger().println("IOException");
            listener.getLogger().println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                listener.getLogger().println("IOException2");
                listener.getLogger().println(e.getMessage());
                e.printStackTrace();
            }
        }
        return result;
    }

    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public FormValidation doCheckName(@QueryParameter String value, @QueryParameter boolean useFrench)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.HelloWorldBuilder_DescriptorImpl_errors_missingName());
            if (value.length() < 4)
                return FormValidation.warning(Messages.HelloWorldBuilder_DescriptorImpl_warnings_tooShort());
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.HelloWorldBuilder_DescriptorImpl_DisplayName();
        }

    }

}
