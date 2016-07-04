//
//  ViewController.swift
//  Cobon Reviews
//
//  Created by Muhammad Hewedy on 7/3/16.
//  Copyright © 2016 Muhammad Hewedy. All rights reserved.
//

import UIKit
import WebKit
import SVProgressHUD

class ViewController: UIViewController, WKNavigationDelegate {

    var webView: WKWebView!
    var backBarButton: UIBarButtonItem!
    var commentsButton: UIBarButtonItem!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        

        // Navigation Bar setup
        self.navigationItem.title = "Cobone Reviews"
        
        self.backBarButton = UIBarButtonItem.init(title: "Back", style: UIBarButtonItemStyle.Plain, target: self, action: #selector(self.back))
        self.commentsButton = UIBarButtonItem.init(title: "Comment", style: UIBarButtonItemStyle.Plain, target: self, action: #selector(self.showComments))
        self.commentsButton.enabled = false
        
        backBarButton.enabled = false;
        
        self.navigationItem.leftBarButtonItem = backBarButton
        self.navigationItem.rightBarButtonItem = commentsButton
        
        // Web View setup
        let wkConfig = WKWebViewConfiguration()
        self.webView = WKWebView.init(frame: self.view.frame, configuration: wkConfig)
        self.webView.allowsBackForwardNavigationGestures = true
        self.webView.navigationDelegate = self
        self.webView.loadRequest(NSURLRequest.init(URL: NSURL.init(string: "https://cobone.com")!))
        self.view.addSubview(self.webView)
        
        // SVProgressHUD setup
        SVProgressHUD.setDefaultStyle(SVProgressHUDStyle.Custom)
        SVProgressHUD.setDefaultAnimationType(SVProgressHUDAnimationType.Native)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // ----
    
    func back()  {
        self.webView.goBack();
    }
    
    func showComments()  {
        // TODO check the url, if a url for deal, then go to comment section, otherwise, show a message to the user

//        self.webView.evaluateJavaScript("window.location.hash='#comments-section'", completionHandler: nil)
        
        self.webView.evaluateJavaScript(loadScript("lib/jquery.min") + ";$('html, body').animate({ scrollTop: $('#comments-section').offset().top}, 2000);"
            , completionHandler: nil)

    }


    // -- WKNavigationDelegate --------------------------
    
    func webView(webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        debugPrint("didStartProvisionalNavigation")
        
        SVProgressHUD.show()
        UIApplication.sharedApplication().networkActivityIndicatorVisible = true
        self.commentsButton.enabled = false
    }
    
    func webView(webView: WKWebView, didCommitNavigation navigation: WKNavigation!) {
        debugPrint("didCommitNavigation")
    }
    
    func webView(webView: WKWebView, didFinishNavigation navigation: WKNavigation!) {
        debugPrint("didFinishNavigation")
        
        SVProgressHUD.dismiss()
        UIApplication.sharedApplication().networkActivityIndicatorVisible = false
        
        if (self.webView.canGoBack){
            self.backBarButton.enabled = true;
        }else{
            self.backBarButton.enabled = false;
        }
        
        injectScript();
    }
    
    func webView(webView: WKWebView, didFailNavigation navigation: WKNavigation!, withError error: NSError) {
        debugPrint("didFailNavigation")
        
        SVProgressHUD.dismiss()
        UIApplication.sharedApplication().networkActivityIndicatorVisible = false
    }
    
    func webView(webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: NSError) {
        debugPrint("didFailProvisionalNavigation")
        
        SVProgressHUD.dismiss()
        UIApplication.sharedApplication().networkActivityIndicatorVisible = false
    }
    
    
    // -- private methods
    
    private func injectScript() {
        debugPrint("try injecting scripts")
        
        let scripts = ["lib/jquery.min", "messages", "translate", "conf", "comments"].map {
            loadScript($0);
            }.joinWithSeparator(" ");
        
        self.webView.evaluateJavaScript(scripts) { (result, error) in
            self.commentsButton.enabled = true
        }
    }
    
    private func loadScript(fileName: String) -> String {
        let path = NSBundle.mainBundle().pathForResource("chrome-extension/" + fileName, ofType: "js");
        
        var content: String?
        do {
            content = try String(contentsOfFile: path!, encoding: NSUTF8StringEncoding)
        } catch _ {
            content = nil
        }
        
        return content!;
    }
}

