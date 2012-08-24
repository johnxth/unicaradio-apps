//
//  SongRequestViewControllerViewController.h
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "../widgets/LoadingDialog.h"

#define kPlistname @"Information.plist"

@interface SongRequestViewController : UIViewController<UITextFieldDelegate>
{
	IBOutlet UIScrollView *scrollView;
    IBOutlet UIView *contentView;

	IBOutlet UITextField *emailTextView;
	IBOutlet UITextField *autoreTextView;
	IBOutlet UITextField *titoloTextView;
	IBOutlet UITextField *captchaTextView;

	LoadingDialog *dialog;

	NSString *captcha;
	NSString *parsedCaptcha;
	
	NSOperationQueue *queue;
	NSTimer *timer;
}

@property (nonatomic, retain) NSString *captcha;

@property (nonatomic, retain) IBOutlet UIScrollView *scrollView;
@property (nonatomic, retain) IBOutlet UIView *contentView;

@property (nonatomic, retain) IBOutlet UITextField *emailTextView;
@property (nonatomic, retain) IBOutlet UITextField *autoreTextView;
@property (nonatomic, retain) IBOutlet UITextField *titoloTextView;
@property (nonatomic, retain) IBOutlet UITextField *captchaTextView;

- (IBAction) sendEmail:(id)sender;

@end
