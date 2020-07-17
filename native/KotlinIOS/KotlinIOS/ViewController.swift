import UIKit
import SharedCode

class ViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, UITextFieldDelegate {

    @IBOutlet private var label: UILabel!

    @IBOutlet weak var DepartureLabel: UILabel!
    @IBOutlet weak var ArrivalLabel: UILabel!
    @IBOutlet weak var DurationLabel: UILabel!
    private let presenter: ApplicationContractPresenter = ApplicationPresenter()
    private var data: [JourneyOption]?
    
    
    @IBOutlet weak var outboundAutocomplete: UITextField!
    @IBOutlet weak var inboundAutocomplete: UITextField!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var Submit: UIButton!
    var pickerData:[String] = [String]()
    
    var autoCompleteCharacterCount = 0
     var timer = Timer()
    
    
    private func setUpTable() {
        let nib = UINib(nibName: "JourneyView", bundle: nil)
        tableView.register(nib, forCellReuseIdentifier: "CUSTOM_CELL")
        tableView.tableFooterView = UIView(frame: .zero)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        presenter.onViewTaken(view: self)
        setUpTable()
        tableView.dataSource = self
        tableView.delegate = self
        self.view.addSubview(self.tableView)
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = 44
        DepartureLabel.isHidden = true
        ArrivalLabel.isHidden = true
        DurationLabel.isHidden = true
    }
    
    override func didReceiveMemoryWarning() {
            super.didReceiveMemoryWarning()
            // Dispose of any resources that can be recreated.
        }
    
    @IBAction func onButtonPress(_ sender: Any) {
        let outboundSelection = self.outboundAutocomplete.text ?? "Canley"
        let inboundSelection =  self.inboundAutocomplete.text ?? "London Euston"

        let date = Date()
        let calendar = Calendar.current
        let year = calendar.component(.year, from: date)
        let month = calendar.component(.month, from: date)
        let day = calendar.component(.day, from: date)
        let hour = calendar.component(.hour, from: date)
        let minutes = calendar.component(.minute, from: date)
        let timeString = String(format: "%04u-%02u-%02uT%02u:%02u:00.000", year, month, day, hour, minutes+1)
        presenter.onButtonPressed(origin: outboundSelection, destination: inboundSelection, time: timeString )
        
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return data?.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "CUSTOM_CELL") as! JourneyView
        let journey = data?[indexPath.row]
        cell.setData(outboundCode: journey!.originStation.crs ,
                     inboundCode: journey!.destinationStation.crs ,
                     outMonth: journey!.departureTime.dateTime.month1 ,
                     outDay: journey!.departureTime.dateTime.dayOfMonth ,
                     outHour: journey!.departureTime.dateTime.hours ,
                     outMinute: journey!.departureTime.dateTime.minutes ,
                     arrivalTime: String(journey!.arrivalTime.dateTime.hours)+":"+String(journey!.arrivalTime.dateTime.minutes) ,
                     duration: String((journey!.arrivalTime.dateTime.minus(other: (journey!.departureTime.dateTime)))/60000),
                     delegate: self)
        return cell
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
            var subString = (textField.text!.capitalized as NSString).replacingCharacters(in: range, with: string) // 2
            subString = formatSubstring(subString: subString)
            
            if subString.count == 0 { // 3 when a user clears the textField
                resetValues(textField)
            } else {
                searchAutocompleteEntriesWIthSubstring(textField, substring: subString) //4
            }
            return true
        }
    
    func formatSubstring(subString: String) -> String {
        let formatted = String(subString.dropLast(autoCompleteCharacterCount)).lowercased().capitalized //5
        return formatted
    }
        
        
        
    func resetValues(_ textField: UITextField) {
        autoCompleteCharacterCount = 0
        textField.text = ""
    }
    
    func searchAutocompleteEntriesWIthSubstring(_ textField:UITextField, substring: String) {
           let userQuery = substring
           let suggestions = getAutocompleteSuggestions(userText: substring) //1
           
           if suggestions.count > 0 {
               timer = .scheduledTimer(withTimeInterval: 0.01, repeats: false, block: { (timer) in //2
                   let autocompleteResult = self.formatAutocompleteResult(substring: substring, possibleMatches: suggestions) // 3
                self.putColourFormattedTextInTextField(textField: textField, autocompleteResult: autocompleteResult, userQuery : userQuery) //4
                self.moveCaretToEndOfUserQueryPosition(textField: textField, userQuery: userQuery) //5
               })
           } else {
               timer = .scheduledTimer(withTimeInterval: 0.01, repeats: false, block: { (timer) in //7
                   textField.text = substring
               })
               autoCompleteCharacterCount = 0
           }
    }
    
    
    func getAutocompleteSuggestions(userText: String) -> [String]{
        var possibleMatches: [String] = []
        for item in presenter.stations { //2
            if let station = item as? NSString {
                let substringRange :NSRange! = station.range(of: userText)
                
                if (substringRange.location == 0)
                {
                    possibleMatches.append(station as String)
                }
            }
        }
        return possibleMatches
    }
    
    func putColourFormattedTextInTextField(textField: UITextField, autocompleteResult: String, userQuery : String) {
        let colouredString: NSMutableAttributedString = NSMutableAttributedString(string: userQuery + autocompleteResult)
        colouredString.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor.green, range: NSRange(location: userQuery.count,length:autocompleteResult.count))
        textField.attributedText = colouredString
    }
    func moveCaretToEndOfUserQueryPosition(textField: UITextField, userQuery : String) {
        if let newPosition = textField.position(from: textField.beginningOfDocument, offset: userQuery.count) {
            textField.selectedTextRange = textField.textRange(from: newPosition, to: newPosition)
        }
        let selectedRange: UITextRange? = textField.selectedTextRange
        textField.offset(from: textField.beginningOfDocument, to: (selectedRange?.start)!)
    }
    func formatAutocompleteResult(substring: String, possibleMatches: [String]) -> String {
        var autoCompleteResult = possibleMatches[0]
        autoCompleteResult.removeSubrange(autoCompleteResult.startIndex..<autoCompleteResult.index(autoCompleteResult.startIndex, offsetBy: substring.count))
        autoCompleteCharacterCount = autoCompleteResult.count
        return autoCompleteResult
    }
}


extension ViewController: ApplicationContractView {
   
    func showData(journeys: [JourneyOption]) {
        self.data = journeys
        DepartureLabel.isHidden = false
        ArrivalLabel.isHidden = false
        DurationLabel.isHidden = false
        tableView.reloadData()
    }
    
    func showAlert(text: String) {
        print(text)
        setLabel(text: text)
    }
    
    func setLabel(text: String) {
        label.text = text
    }
    
    func openWebpage(url: String) {
        UIApplication.shared.open(NSURL(string:url)! as URL)
    }
}

extension ViewController: CustomTableCellDelegate {
    
    func onBuyButtonTapped(outboundCode: String, inboundCode: String, outMonth: Int32, outDay: Int32, outHour: Int32, outMinute: Int32, returnSymbol: Bool = true) {
        presenter.onBuyButton(outbound: outboundCode, inbound: inboundCode, month: outMonth, day: outDay, hour: outHour, minutes: outMinute, returnBool: returnSymbol)    }
}
