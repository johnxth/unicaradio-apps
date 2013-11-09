//
//  SongRequestViewControllerViewController.m
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "SongRequestViewController.h"

#import "../operations/RequestSongOperation.h"
#import "../models/SongRequest.h"
#import "../enums/Error.h"

#import "FormTextFieldTableViewCell.h"
#import "ValidTooltipView.h"
#import "InvalidTooltipView.h"
#import "SubmitButtonTableViewCell.h"

#import "AuthorTitleValidatorCondition.h"

#import "US2Validator.h"
#import "US2ValidatorTextView.h"
#import "US2ValidatorTextField.h"
#import "US2ValidatorEmail.h"

#import "SystemUtils.h"

@interface SongRequestViewController ()

@end

@implementation SongRequestViewController

@synthesize tableView;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"CONTROLLER_TITLE_SONGREQUEST", @"");
        self.tabBarItem.image = [UIImage imageNamed:@"song"];
		queue = [[NSOperationQueue alloc] init];
		[queue setMaxConcurrentOperationCount: 1];
		isTableViewInitialized = NO;

		[self initButtonBarItems];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	NSLog(@"SongRequestViewController - viewDidLoad");

	[self.tableView setBackgroundView:nil];
	[self.tableView setBackgroundColor:[UIColor blackColor]];
	self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;

	[self initTextFields];
}

- (void) initTextFields
{
	textFields = [[NSMutableArray alloc] init];

	NSString *email = @"";
	NSString *filePath = [self getDataFilePath];
	if([[NSFileManager defaultManager] fileExistsAtPath:filePath]) {
		NSDictionary *dict = [[NSDictionary alloc] initWithContentsOfFile:filePath];
		email = [dict objectForKey:@"email"];
	}

	US2ValidatorTextField *textField = [self createTextField];
	textField.validator = [[US2ValidatorEmail alloc] init];
	textField.placeholder = NSLocalizedString(@"EMAIL_PLACEHOLDER", @"");
	textField.text = email;
	[textField setKeyboardType:UIKeyboardTypeEmailAddress];
	[textField setAutocapitalizationType:UITextAutocapitalizationTypeNone];
	[textField setAutocorrectionType:UITextAutocorrectionTypeNo];
	[textFields addObject:textField];

	US2ValidatorTextField *textField2 = [self createTextField];
	textField2.validator = [[AuthorTitleValidatorCondition alloc] init];
	textField2.placeholder = NSLocalizedString(@"AUTHOR_PLACEHOLDER", @"");
	[textFields addObject:textField2];

	US2ValidatorTextField *textField3 = [self createTextField];
	textField3.validator = [[AuthorTitleValidatorCondition alloc] init];
	textField3.placeholder = NSLocalizedString(@"TITLE_PLACEHOLDER", @"");
	[textFields addObject:textField3];
}

- (US2ValidatorTextField *) createTextField
{
	US2ValidatorTextField *textField = [[US2ValidatorTextField alloc] init];
	textField.delegate = self;
	textField.shouldAllowViolations = YES;
	textField.validateOnFocusLossOnly = NO;
	textField.text = @"";
	textField.placeholder = @"";
	textField.validatorUIDelegate = self;

	return textField;
}

- (void)viewDidUnload
{
    [super viewDidUnload];
}

- (void) viewDidAppear:(BOOL)animated
{
	[super viewDidAppear:animated];
	NSLog(@"SongRequestViewController - viewDidAppear");

	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(receiveNotification:) name:@"SendEmail" object:nil];

	[self clearForm];

	if(isTableViewInitialized) {
		[self reloadButtonRow];
	}
	isTableViewInitialized = YES;
}

- (void) viewDidDisappear:(BOOL)animated
{
	[[NSNotificationCenter defaultCenter] removeObserver:self];

	[super viewDidDisappear:animated];
}

- (BOOL) textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

- (void) receiveNotification: (NSNotification *)notification
{
	if([[notification name] isEqualToString:@"SendEmail"]) {
		[self receiveSendEmailNotification:notification];
	}
}

- (void) receiveSendEmailNotification:(NSNotification *)notification
{
	NSLog(@"called receiveSendEmailNotification");

	[self performSelectorOnMainThread:@selector(sendEmailCompleted:) withObject:[notification object] waitUntilDone:YES];
}

- (void) showLoadingDialog
{
	NSString *dialogTitle = NSLocalizedString(@"DIALOG_LOADING", @"");
	dialog = [[LoadingDialog alloc] initWithTitle:dialogTitle message:nil delegate:nil cancelButtonTitle:nil otherButtonTitles:nil];
	[dialog show];
}

- (void) sendEmail
{
	[self saveEmailAddress];

	SongRequest *request = [[SongRequest alloc] init];
	request.email = [[textFields objectAtIndex:EMAIL_POSITION] text];
	request.title = [[textFields objectAtIndex:TITLE_POSITION] text];
	request.author = [[textFields objectAtIndex:AUTHOR_POSITION] text];

	NSLog(@"request: %@", [request toJSONString]);

	[self showLoadingDialog];
	RequestSongOperation *operation = [[RequestSongOperation alloc] initWithSongRequest:request];
	[queue addOperation:operation];
}

- (void) saveEmailAddress
{
	NSString *email = [[textFields objectAtIndex:EMAIL_POSITION] text];
	NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
	[dict setObject:email forKey:@"email"];
	[dict writeToFile:[self getDataFilePath] atomically:YES];
}

- (void) sendEmailCompleted:(NSDictionary *)serverResponse
{
	[dialog dismiss];
	if(serverResponse == nil) {
		NSString *title = NSLocalizedString(@"DIALOG_SEND_FAILED_TITLE", @"");
		NSString *message = NSLocalizedString(@"DIALOG_SEND_FAILED_MESSAGE", @"");
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil];
		[alert show];
		return;
	}

	NSNumber *errorCode = [serverResponse objectForKey:@"errorCode"];
	NSLog(@"errorCode: %d", [errorCode integerValue]);
	if([errorCode intValue] == NO_ERROR) {
		NSString *title = NSLocalizedString(@"DIALOG_SEND_OK_TITLE", @"");
		NSString *message = NSLocalizedString(@"DIALOG_SEND_OK_TITLE_MESSAGE", @"");
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil];
		[alert show];
		[self clearForm];
	} else {
		NSString *title;
		NSString *message;
		if([errorCode intValue] == OPERATION_FORBIDDEN) {
			title = NSLocalizedString(@"DIALOG_WAIT_BEFORE_SEND_AGAIN_TITLE", @"");
			message = NSLocalizedString(@"DIALOG_WAIT_BEFORE_SEND_AGAIN_MESSAGE", @"");
		} else if([errorCode intValue] == INTERNAL_DOWNLOAD_ERROR) {
			title = NSLocalizedString(@"DIALOG_CHECK_CONNECTION_TITLE", @"");
			message = NSLocalizedString(@"DIALOG_CHECK_CONNECTION_MESSAGE", @"");
		} else {
			title = NSLocalizedString(@"DIALOG_SEND_FAILED_TITLE", @"");
			message = NSLocalizedString(@"DIALOG_SEND_FAILED_MESSAGE", @"");
		}

		UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil];
		[alert show];
	}
}

- (void) clearForm
{
	for(NSUInteger i = 0; i < textFields.count; i++) {
		if(i == EMAIL_POSITION) {
			continue;
		}

		US2ValidatorTextField *textUI = [textFields objectAtIndex:i];
		textUI.text = @"";

		id cell = textUI.superview.superview;
		if([cell isKindOfClass:[FormTextFieldTableViewCell class]]) {
			FormTextFieldTableViewCell *formTextFieldTableViewCell = (FormTextFieldTableViewCell *)cell;
			[formTextFieldTableViewCell updateValidationIconByValidStatus:kFormTableViewCellStatusWaiting];
		}
	}
}

- (FormTextFieldTableViewCell *)formTextFieldTableViewCellFromTableView:(UITableView *)_tableView
{
	FormTextFieldTableViewCell *cell = [_tableView dequeueReusableCellWithIdentifier:@"FormTextFieldTableViewCellReuseIdentifier"];
    //UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"FormTextFieldTableViewCellReuseIdentifier"];
    if(nil == cell) {
        cell = [[FormTextFieldTableViewCell alloc] initWithReuseIdentifier:@"FormTextFieldTableViewCellReuseIdentifier"];
    }

    return cell;
}

/**
 * Creating new submit button table view cell or re-use it
 */
- (SubmitButtonTableViewCell *)submitButtonTableViewCellFromTableView:(UITableView *)_tableView
{
    SubmitButtonTableViewCell *cell;
	//cell = [_tableView dequeueReusableCellWithIdentifier:@"SubmitButtonTableViewCellReuseIdentifier"];
    //if(nil == cell) {
        cell = [[SubmitButtonTableViewCell alloc] initWithReuseIdentifier:@"SubmitButtonTableViewCellReuseIdentifier"];
    //}

    return cell;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)_tableView
{
    return 2;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
	return @"";
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	if(section == 0) {
		return 3;
	} else {
		return 1;
	}
}

- (UITableViewCell *)tableView:(UITableView *)_tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	if(indexPath.section == 1) {
        SubmitButtonTableViewCell *cell = [self submitButtonTableViewCellFromTableView:tableView];
        [cell.button addTarget:self action:@selector(submitButtonTouched:) forControlEvents:UIControlEventTouchUpInside];
		cell.backgroundColor = [UIColor clearColor];

        return cell;
    }

	NSInteger row = indexPath.row;
	US2ValidatorTextField *textField = [textFields objectAtIndex:row];

	FormTextFieldTableViewCell *cell;
	if(row == 0) {
		cell = [self formTextFieldTableViewCellFromTableView:_tableView];
		cell.textUI = textField;

		cell.textLabel.text = NSLocalizedString(@"EMAIL_LABEL", @"");
	} else if(row == 1) {
		cell = [self formTextFieldTableViewCellFromTableView:_tableView];
		cell.textUI = textField;

		cell.textLabel.text = NSLocalizedString(@"AUTHOR_LABEL", @"");
	} else if(row == 2) {
		cell = [self formTextFieldTableViewCellFromTableView:_tableView];
		cell.textUI = textField;

		cell.textLabel.text = NSLocalizedString(@"TITLE_LABEL", @"");
	}

	cell.detailTextLabel.text = NSLocalizedString(@"REQUIRED_FIELD", @"");
	cell.delegate = self;

	return cell;
}

- (NSString *) getDataFilePath
{
	NSArray *path = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
	NSString *documentsDirectory = [path objectAtIndex:0];
	return [documentsDirectory stringByAppendingPathComponent:kPlistname];
}

- (void) dealloc
{
	[[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - Validator text field protocol

- (BOOL) textFieldShouldBeginEditing:(UITextField *)textField
{
    // Hide tooltip
    if(nil != _tooltipView && ![_tooltipConnectedTextUI isEqual:textField]) {
        [_tooltipView removeFromSuperview];
        _tooltipView = nil;
    }

    _tooltipConnectedTextUI = nil;

    return YES;
}

- (BOOL) textViewShouldBeginEditing:(UITextView *)textView
{
    // Hide tooltip
    if(nil != _tooltipView && ![_tooltipConnectedTextUI isEqual:textView]) {
        [_tooltipView removeFromSuperview];
        _tooltipView = nil;
    }

    _tooltipConnectedTextUI = nil;

    return YES;
}

/**
 Called for every valid or violated state change
 React to this information by showing up warnings or disabling a 'send' button e.g.
 */
- (void) validatorUI:(id <US2ValidatorUIProtocol>)validatorUI changedValidState:(BOOL)isValid
{
    NSLog(@"validatorUI changedValidState: %d", isValid);
    
    // 1st super view UITableViewCellContentView
    // 2nd super view FormTextFieldTableViewCell
    id cell = ((UIView *)validatorUI).superview.superview;
    if([cell isKindOfClass:[FormTableViewCell class]]) {
        FormTableViewCell *formTableViewCell = (FormTableViewCell *)cell;
        kFormTableViewCellStatus status = isValid == YES ? kFormTableViewCellStatusValid : kFormTableViewCellStatusInvalid;
        status = validatorUI.text.length == 0 ? kFormTableViewCellStatusWaiting : status;
        [formTableViewCell updateValidationIconByValidStatus:status];
    }

    // Hide tooltip
    if(isValid) {
        [self dismissTooltip];
    }
}

/**
 Called on every violation of the highest prioritised validator condition.
 Update UI like showing alert messages or disabling buttons.
 */
- (void)validatorUI:(id <US2ValidatorUIProtocol>)validatorUI violatedConditions:(US2ConditionCollection *)conditions
{
    NSLog(@"validatorUI violatedConditions: \n%@", conditions);
}

/**
 Update violation status of text field after ending editing
 */
- (void)textFieldDidEndEditing:(US2ValidatorTextField *)validatorTextField
{
    id cell = validatorTextField.superview.superview;
    if([cell isKindOfClass:[FormTextFieldTableViewCell class]]) {
        FormTextFieldTableViewCell *formTextFieldTableViewCell = (FormTextFieldTableViewCell *)cell;
        kFormTableViewCellStatus status = validatorTextField.isValid == YES ? kFormTableViewCellStatusValid : kFormTableViewCellStatusInvalid;

		status = validatorTextField.text.length == 0 ? kFormTableViewCellStatusWaiting : status;

        [formTextFieldTableViewCell updateValidationIconByValidStatus:status];
    }
}

/**
 Update violation status of text view after ending editing
 */
- (void)textViewDidEndEditing:(US2ValidatorTextView *)validatorTextView
{
    id cell = validatorTextView.superview.superview;
    if([cell isKindOfClass:[FormTableViewCell class]]) {
        FormTableViewCell *formTableViewCell = (FormTableViewCell *)cell;
        kFormTableViewCellStatus status = validatorTextView.isValid == YES ? kFormTableViewCellStatusValid : kFormTableViewCellStatusInvalid;

		status = validatorTextView.text.length == 0 ? kFormTableViewCellStatusWaiting : status;

        [formTableViewCell updateValidationIconByValidStatus:status];
    }
}

#pragma mark - Form table view cell delegate

- (void)formTableViewCell:(FormTableViewCell *)cell touchedIconButton:(UIButton *)button aligningTextUI:(id <US2ValidatorUIProtocol>)textUI
{
	NSLog(@"formTableViewCell:(FormTableViewCell *)cell touchedIconButton:(UIButton *)button aligningTextUI:(id <US2ValidatorUIProtocol>)textUI");
    // Show tooltip if status changed to invalid
    // Hide tooltip if status changed to valid
    if(nil != _tooltipView) {
		NSLog(@"removing");
        [_tooltipView removeFromSuperview];
        _tooltipView = nil;
    }

    // Do not show tooltip again, because it was toggled off
    if([_tooltipConnectedTextUI isEqual:textUI]) {
        _tooltipConnectedTextUI = nil;

        return;
    }

    // Determine point where to add the tooltip
    CGPoint point = [button convertPoint:CGPointMake(0.0, button.frame.size.height - 4.0) toView: tableView];

    // Create tooltip
    // Set text to tooltip
    US2Validator *validator = [textUI validator];
    US2ConditionCollection *conditionCollection = [validator checkConditions:[textUI text]];
	float x;
	if(![DeviceUtils isPhone]) {
		x = 675.0;
	} else {
		if([DeviceUtils isLandscape] && [DeviceUtils is4InchRetinaIPhone]) {
			x = 254.0;
		} else if([DeviceUtils isLandscape]) {
			x = 168.0;
		} else {
			x = 6.0;
		}
	}
	
	if(SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(@"7.0")) {
		if([DeviceUtils isIPad]) {
			x += 45;
		} else {
			x += 10;
		}
	}

    CGRect tooltipViewFrame = CGRectMake(x, point.y, 309.0, _tooltipView.frame.size.height);
    if (nil == conditionCollection) {
        _tooltipView       = [[ValidTooltipView alloc] init];
        _tooltipView.frame = tooltipViewFrame;

        _tooltipView.text = NSLocalizedString(@"TOOLTIP_OK", @"");
    } else {
        _tooltipView       = [[InvalidTooltipView alloc] init];
        _tooltipView.frame = tooltipViewFrame;

        // Get first violation
        US2Condition *violatedCondition = [conditionCollection conditionAtIndex:0];
        _tooltipView.text = [violatedCondition localizedViolationString];
    }

    [tableView addSubview:_tooltipView];

    // Remember text UI to which the tooltip was connected
    _tooltipConnectedTextUI = textUI;
}

- (void)dismissTooltip
{
    if(nil != _tooltipView) {
        [_tooltipView removeFromSuperview];
        _tooltipView = nil;
    }

    _tooltipConnectedTextUI = nil;
}

#pragma mark - Submit button

- (void)submitButtonTouched:(UIButton *)button
{
	NSLog(@"x: %f, y: %f, w: %f, h: %f", button.frame.origin.x, button.frame.origin.y, button.frame.size.width, button.frame.size.height);
	
    // Create string which will contain the first error in form
    NSMutableString *errorString = [NSMutableString string];

    // Validate every text UI in custom text UI collection
    for(NSUInteger i = 0; i < textFields.count; i++) {
        id <US2ValidatorUIProtocol> textUI = [textFields objectAtIndex:i];
        id cell = ((UIView *)textUI).superview.superview;
        if ([cell isKindOfClass:[FormTableViewCell class]]) {
            FormTableViewCell *formTableViewCell = (FormTextFieldTableViewCell *)cell;
            kFormTableViewCellStatus status = textUI.isValid == YES ? kFormTableViewCellStatusValid : kFormTableViewCellStatusInvalid;
            [formTableViewCell updateValidationIconByValidStatus:status];

            // If the text UI has invalid text remember the violated condition with highest priority
            if(textUI.isValid == NO && errorString.length == 0) {
                US2Validator *validator = [textUI validator];
                US2ConditionCollection *conditionCollection = [validator checkConditions:[textUI text]];
                US2Condition *violatedCondition = [conditionCollection conditionAtIndex:0];

                NSMutableString *violatedString = [NSMutableString string];
                [violatedString appendString:formTableViewCell.textLabel.text];
                [violatedString appendString:@": "];
                [violatedString appendString:[violatedCondition localizedViolationString]];
                [errorString appendString:violatedString];
            }
        }
    }

    // Show alert if there was an invalid text in UI
    if(errorString.length > 0) {
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle: NSLocalizedString(@"DIALOG_INVALID_TEXT", @"")
                                                            message: errorString
                                                           delegate: self
                                                  cancelButtonTitle: @"OK"
                                                  otherButtonTitles: nil, nil];
        [alertView show];
    } else {
		[self sendEmail];
	}
}

-(void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation) fromInterfaceOrientation
{
	[self reloadButtonRow];
}

-(void) reloadButtonRow
{
	NSIndexPath *indexPath = [NSIndexPath indexPathForRow:0 inSection:1];
    [tableView reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
}

@end
