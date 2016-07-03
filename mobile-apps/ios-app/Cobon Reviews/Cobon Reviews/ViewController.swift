//
//  ViewController.swift
//  Cobon Reviews
//
//  Created by Muhammad Hewedy on 7/3/16.
//  Copyright © 2016 Muhammad Hewedy. All rights reserved.
//

import UIKit
import SVProgressHUD

class ViewController: UIViewController, UIWebViewDelegate {

    @IBOutlet weak var webView: UIWebView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view, typically from a nib.
        self.webView.delegate = self
        self.webView.loadRequest(NSURLRequest.init(URL: NSURL.init(string: "https://cobone.com")!))
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    
    // -- UIWebViewDelegate
    
    func webViewDidStartLoad(webView: UIWebView) {
        SVProgressHUD.show()
        UIApplication.sharedApplication().networkActivityIndicatorVisible = true
    }
    
    func webViewDidFinishLoad(webView: UIWebView) {
        SVProgressHUD.dismiss()
        UIApplication.sharedApplication().networkActivityIndicatorVisible = false
    }
}

